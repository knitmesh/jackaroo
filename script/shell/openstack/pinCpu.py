import libvirt_qemu
import psutil
import threading

import eventlet
from eventlet import tpool

from oslo.config import cfg
from nova.i18n import _
from nova.i18n import _LE
from nova.i18n import _LI
from nova.i18n import _LW
from nova.openstack.common import importutils
from nova.virt import driver
from nova.openstack.common import log as logging
from nova.virt import event as virtevent

from nova.api.metadata import base as instance_metadata
from nova import context as nova_context
from nova import exception

libvirt = None

LOG = logging.getLogger(__name__)

libvirt_opts = [
    cfg.StrOpt('virt_type',
               default='kvm',
               help='Libvirt domain type (valid options are: '
                    'kvm, lxc, qemu, uml, xen)'),
    cfg.StrOpt('connection_uri',
               default='',
               help='Override the default libvirt URI '
                    '(which is dependent on virt_type)'),
    cfg.ListOpt('base_path',
                default='/var/run/libvirt/qemu/',
                help='The path for get process of instance')
    ]

CONF = cfg.CONF
CONF.register_opts(libvirt_opts, 'libvirt')

DISABLE_REASON_UNDEFINED = 'None'

class LibvirtDriver():
    
    def __init__(self, read_only=False):
        global libvirt
        if libvirt is None:
            libvirt = importutils.import_module('libvirt')       

        self._wrapped_conn_lock = threading.Lock()
        self.read_only = read_only
    
    def _get_new_connection(self):
        # call with _wrapped_conn_lock held
        LOG.debug('Connecting to libvirt: %s', self.uri())
        wrapped_conn = None

        try:
            wrapped_conn = self._connect(self.uri(), self.read_only)
        finally:
            # Enabling the compute service, in case it was disabled
            # since the connection was successful.
            disable_reason = DISABLE_REASON_UNDEFINED
            if not wrapped_conn:
                disable_reason = 'Failed to connect to libvirt'
            self._set_host_enabled(bool(wrapped_conn), disable_reason)

        self._wrapped_conn = wrapped_conn
        self._skip_list_all_domains = False

        try:
            LOG.debug("Registering for lifecycle events %s", self)
            wrapped_conn.domainEventRegisterAny(
                None,
                libvirt.VIR_DOMAIN_EVENT_ID_LIFECYCLE,
                self._event_lifecycle_callback,
                self)
        except Exception as e:
            LOG.warn(_LW("URI %(uri)s does not support events: %(error)s"),
                     {'uri': self.uri(), 'error': e})

        try:
            LOG.debug("Registering for connection events: %s", str(self))
            wrapped_conn.registerCloseCallback(self._close_callback, None)
        except (TypeError, AttributeError) as e:
            # NOTE: The registerCloseCallback of python-libvirt 1.0.1+
            # is defined with 3 arguments, and the above registerClose-
            # Callback succeeds. However, the one of python-libvirt 1.0.0
            # is defined with 4 arguments and TypeError happens here.
            # Then python-libvirt 0.9 does not define a method register-
            # CloseCallback.
            LOG.debug("The version of python-libvirt does not support "
                      "registerCloseCallback or is too old: %s", e)
        except libvirt.libvirtError as e:
            LOG.warn(_LW("URI %(uri)s does not support connection"
                         " events: %(error)s"),
                     {'uri': self.uri(), 'error': e})

        return wrapped_conn
    
    def _get_connection(self):
        # multiple concurrent connections are protected by _wrapped_conn_lock
        with self._wrapped_conn_lock:
            #wrapped_conn = self._wrapped_conn
            wrapped_conn = None 
            if not wrapped_conn or not self._test_connection(wrapped_conn):
                wrapped_conn = self._get_new_connection()

        return wrapped_conn

    _conn = property(_get_connection)

    def _close_callback(self, conn, reason, opaque):
        close_info = {'conn': conn, 'reason': reason}
        self._queue_event(close_info)

    @staticmethod
    def _test_connection(conn):
        try:
            conn.getLibVersion()
            return True
        except libvirt.libvirtError as e:
            if (e.get_error_code() in (libvirt.VIR_ERR_SYSTEM_ERROR,
                                       libvirt.VIR_ERR_INTERNAL_ERROR) and
                e.get_error_domain() in (libvirt.VIR_FROM_REMOTE,
                                         libvirt.VIR_FROM_RPC)):
                LOG.debug('Connection to libvirt broke')
                return False
            raise

    @staticmethod
    def uri():
        if CONF.libvirt.virt_type == 'uml':
            uri = CONF.libvirt.connection_uri or 'uml:///system'
        elif CONF.libvirt.virt_type == 'xen':
            uri = CONF.libvirt.connection_uri or 'xen:///'
        elif CONF.libvirt.virt_type == 'lxc':
            uri = CONF.libvirt.connection_uri or 'lxc:///'
        else:
            uri = CONF.libvirt.connection_uri or 'qemu:///system'
        return uri

    @staticmethod
    def _connect_auth_cb(creds, opaque):
        if len(creds) == 0:
            return 0
        raise exception.NovaException(
            _("Can not handle authentication request for %d credentials")
            % len(creds))

    @staticmethod
    def _connect(uri, read_only):
        auth = [[libvirt.VIR_CRED_AUTHNAME,
                 libvirt.VIR_CRED_ECHOPROMPT,
                 libvirt.VIR_CRED_REALM,
                 libvirt.VIR_CRED_PASSPHRASE,
                 libvirt.VIR_CRED_NOECHOPROMPT,
                 libvirt.VIR_CRED_EXTERNAL],
                LibvirtDriver._connect_auth_cb,
                None]

        try:
            flags = 0
            if read_only:
                flags = libvirt.VIR_CONNECT_RO
            # tpool.proxy_call creates a native thread. Due to limitations
            # with eventlet locking we cannot use the logging API inside
            # the called function.
            return tpool.proxy_call(
                (libvirt.virDomain, libvirt.virConnect),
                libvirt.openAuth, uri, auth, flags)
        except libvirt.libvirtError as ex:
            LOG.exception(_LE("Connection to libvirt failed: %s"), ex)
            payload = dict(ip=LibvirtDriver.get_host_ip_addr(),
                           method='_connect',
                           reason=ex)
            rpc.get_notifier('compute').error(nova_context.get_admin_context(),
                                              'compute.libvirt.error',
                                              payload)
            raise exception.HypervisorUnavailable(host=CONF.host)
    def _set_host_enabled(self, enabled,
                          disable_reason=DISABLE_REASON_UNDEFINED):
        """Enables / Disables the compute service on this host.

           This doesn't override non-automatic disablement with an automatic
           setting; thereby permitting operators to keep otherwise
           healthy hosts out of rotation.
        """

        status_name = {True: 'disabled',
                       False: 'enabled'}

        disable_service = not enabled

        ctx = nova_context.get_admin_context()
        try:
            service = objects.Service.get_by_compute_host(ctx, CONF.host)

            if service.disabled != disable_service:
                # Note(jang): this is a quick fix to stop operator-
                # disabled compute hosts from re-enabling themselves
                # automatically. We prefix any automatic reason code
                # with a fixed string. We only re-enable a host
                # automatically if we find that string in place.
                # This should probably be replaced with a separate flag.
                if not service.disabled or (
                        service.disabled_reason and
                        service.disabled_reason.startswith(DISABLE_PREFIX)):
                    service.disabled = disable_service
                    service.disabled_reason = (
                       DISABLE_PREFIX + disable_reason
                       if disable_service else DISABLE_REASON_UNDEFINED)
                    service.save()
                    LOG.debug('Updating compute service status to %s',
                              status_name[disable_service])
                else:
                    LOG.debug('Not overriding manual compute service '
                              'status with: %s',
                              status_name[disable_service])
        except exception.ComputeHostNotFound:
            LOG.warn(_LW('Cannot update service status on host: %s,'
                         'since it is not registered.'), CONF.host)
        except Exception:
            LOG.warn(_LW('Cannot update service status on host: %s,'
                         'due to an unexpected exception.'), CONF.host,
                     exc_info=True)
    @staticmethod
    def _event_lifecycle_callback(conn, dom, event, detail, opaque):
        """Receives lifecycle events from libvirt.

        NB: this method is executing in a native thread, not
        an eventlet coroutine. It can only invoke other libvirt
        APIs, or use self.queue_event(). Any use of logging APIs
        in particular is forbidden.
        """

        self = opaque

        uuid = dom.UUIDString()
        transition = None
        if event == libvirt.VIR_DOMAIN_EVENT_STOPPED:
            transition = virtevent.EVENT_LIFECYCLE_STOPPED
        elif event == libvirt.VIR_DOMAIN_EVENT_STARTED:
            transition = virtevent.EVENT_LIFECYCLE_STARTED
        elif event == libvirt.VIR_DOMAIN_EVENT_SUSPENDED:
            transition = virtevent.EVENT_LIFECYCLE_PAUSED
        elif event == libvirt.VIR_DOMAIN_EVENT_RESUMED:
            transition = virtevent.EVENT_LIFECYCLE_RESUMED

        if transition is not None:
            self._queue_event(virtevent.LifecycleEvent(uuid, transition))
    def _list_instance_domains_fast(self, only_running=True):
        # The modern (>= 0.9.13) fast way - 1 single API call for all domains
        #import ipdb;ipdb.set_trace()
        flags = libvirt.VIR_CONNECT_LIST_DOMAINS_ACTIVE
        if not only_running:
            flags = flags | libvirt.VIR_CONNECT_LIST_DOMAINS_INACTIVE
        return self._conn.listAllDomains(flags)


if  __name__ == "__main__":
    try:
        physics_cpu_count = psutil.cpu_count(logical=False)
        cpumap = tuple(range(0,physics_cpu_count))
        qemu_base_path = CONF.libvirt.base_path
        virt_dom = LibvirtDriver()._list_instance_domains_fast()
        for i in range(len(virt_dom)):
			vcpus = virt_dom[i].vcpus()
			#vcpus = virt_dom.vcpus()
			for j in range(len(vcpus[0])):
                virtual_cpu,real_cpu = vcpus[0][j][0],vcpus[0][j][len(vcpus[0])-1]
                LOG.info(_LW('---zztest pin cpu start---- '
                    'virtual_cpu=%(virtual_cpu)s real_cpu=%(real_cpu)s cpumap=%(cpumap)s'),
                    {'virtual_cpu':virtual_cpu,
                     'real_cpu':real_cpu,
                     'cpumap':cpumap})
                try:
                    res = virt_dom.pinVcpu(virtual_cpu, cpumap)
                    if res != 0:
                        vcpu_quota = dict(vcpu_quota = 50000)
                        virt_dom[i].setSchedulerParameters(vcpu_quota)
				except virt_dom.exceptions as e:
				   LOG.error(_LE('zztest failed to pin and scheduler cpu of  %(instance)s:'
							'%(e)s'),{'instance':virt_dom.name(),'e':e})
    except virt_dom.exceptions as e:
        LOG.error(_LE('zztest failed to pin and scheduler cpu of  %(instance)s:'
                                            '%(e)s'),
                       {'instance':virt_dom.name(),'e':e}) 


