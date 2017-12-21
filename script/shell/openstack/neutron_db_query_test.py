import datetime
import random
import time

import sys
from pprint import pprint as pp

import eventlet
eventlet.monkey_patch()

from oslo.config import cfg

from neutron.common import config
from neutron import service

from neutron.openstack.common import gettextutils
from neutron.openstack.common import log as logging
gettextutils.install('neutron', lazy=True)

LOG = logging.getLogger(__name__)

from oslo.config import cfg
from oslo.db import exception as db_exc
import sqlalchemy as sa
from sqlalchemy import func
from sqlalchemy import orm
from sqlalchemy.orm import exc
from sqlalchemy.orm import joinedload
from sqlalchemy import sql

from neutron.common import constants
from neutron.common import utils as n_utils
from neutron import context as n_ctx
from neutron.db import agents_db
from neutron.db import agentschedulers_db
from neutron.db import l3_attrs_db
from neutron.db import model_base
from neutron.extensions import l3agentscheduler
from neutron import manager
from neutron.openstack.common.gettextutils import _LI, _LW
from neutron.openstack.common import log as logging
from neutron.openstack.common import loopingcall
from neutron.openstack.common import timeutils
import time

from neutron.api.rpc.handlers import l3_rpc

config.init(sys.argv[1:])

context = n_ctx.get_admin_context()

core_plugin = manager.NeutronManager.get_plugin()
subnet_ids=set([u'35eb4730-3230-4ace-b01b-2faf49a77c61', u'4f3649f7-e434-4dc7-872c-edb5844e8f6c', u'ae8720de-b61f-4046-9d52-9a167ba97a91', u'aab013e4-3da9-4dbf-9080-8ed069fe2164', u'f337c1e6-76eb-457b-be87-b0a8e7c10ea9', u'50ffd24d-bb79-4699-8bad-06ba470295f3', u'cb854358-58bb-4ece-ba02-9b6bacd2339c', u'b3867eb4-e903-4649-9442-80bb0209a025', u'e8eec67f-37cd-47a2-9d2e-b7647fe26495', u'17b6256e-5bbc-4055-a17d-887945ab117d', u'a8fdc483-a5c0-4430-b747-0fdb722b9941', u'08793e3b-50f0-4a09-8da1-ce3569d4db97', u'd22d3cbd-79df-4f41-89da-480f7c7ba785', u'd070580f-9d98-46f3-bf7d-b8b1ec8970be', u'e9666b0b-c26a-4398-bf00-448c6ba5ecfd', u'ae452ac7-c9c5-4e77-9529-fe9d8d924f8c', u'896644e1-9922-4c97-ac60-ee5d5192ce52', u'dbb64796-38dd-495f-adc4-af70bf41b7ae', u'21bced6c-bd53-419a-92eb-13dded30e6d3', u'27978544-c3a8-465d-b9e6-cc0d53d61617', u'ee79672b-6211-48e1-bb80-930463a31ee7', u'25d3ac52-a5b7-40b8-a16d-7556ea2d5514', u'ec05e05c-5ad7-4020-95f7-7d270dcb6432', u'c4faf7a9-4b7c-4b09-bfba-8e5d55b99770', u'ffc10697-96ae-4a50-bbf5-2db15101a7bc', u'd0736e7b-ff3c-48dc-9dd9-14401bdde59f', u'dca4c082-cf37-4370-b3bf-4a775d315fad', u'443b8791-6bc8-498e-8b01-f553f5622ad7', u'60d34851-89ed-47d6-bdaf-92c51d363cbf', u'fdebdd3c-4928-47bd-8081-d19ba91f6652', u'46f0921f-dbea-4c64-bc18-3ec5ca0a0716', u'009617d9-c555-4119-b974-11b2f54f3fc7'])

sql = '''
SELECT 
    ports.tenant_id AS ports_tenant_id, ports.id AS ports_id, 
    ports.name AS ports_name, ports.network_id AS ports_network_id, 
    ports.mac_address AS ports_mac_address, ports.admin_state_up AS ports_admin_state_up, 
    ports.status AS ports_status, ports.device_id AS ports_device_id, 
    ports.device_owner AS ports_device_owner, 
    ipallocations_1.port_id AS ipallocations_1_port_id, ipallocations_1.ip_address AS ipallocations_1_ip_address, 
    ipallocations_1.subnet_id AS ipallocations_1_subnet_id, ipallocations_1.network_id AS ipallocations_1_network_id, 
    ml2_port_bindings_1.port_id AS ml2_port_bindings_1_port_id, ml2_port_bindings_1.host AS ml2_port_bindings_1_host, 
    ml2_port_bindings_1.vnic_type AS ml2_port_bindings_1_vnic_type, ml2_port_bindings_1.profile AS ml2_port_bindings_1_profile, 
    ml2_port_bindings_1.vif_type AS ml2_port_bindings_1_vif_type, ml2_port_bindings_1.vif_details AS ml2_port_bindings_1_vif_details, 
    ml2_port_bindings_1.driver AS ml2_port_bindings_1_driver, ml2_port_bindings_1.segment AS ml2_port_bindings_1_segment, 
    ml2_dvr_port_bindings_1.port_id AS ml2_dvr_port_bindings_1_port_id, ml2_dvr_port_bindings_1.host AS ml2_dvr_port_bindings_1_host, 
    ml2_dvr_port_bindings_1.router_id AS ml2_dvr_port_bindings_1_router_id, ml2_dvr_port_bindings_1.vif_type AS ml2_dvr_port_bindings_1_vif_type, 
    ml2_dvr_port_bindings_1.vif_details AS ml2_dvr_port_bindings_1_vif_details, ml2_dvr_port_bindings_1.vnic_type AS ml2_dvr_port_bindings_1_vnic_type, 
    ml2_dvr_port_bindings_1.profile AS ml2_dvr_port_bindings_1_profile, ml2_dvr_port_bindings_1.cap_port_filter AS ml2_dvr_port_bindings_1_cap_port_filter, 
    ml2_dvr_port_bindings_1.driver AS ml2_dvr_port_bindings_1_driver, ml2_dvr_port_bindings_1.segment AS ml2_dvr_port_bindings_1_segment, 
    ml2_dvr_port_bindings_1.status AS ml2_dvr_port_bindings_1_status, extradhcpopts_1.id AS extradhcpopts_1_id, 
    extradhcpopts_1.port_id AS extradhcpopts_1_port_id, extradhcpopts_1.opt_name AS extradhcpopts_1_opt_name, 
    extradhcpopts_1.opt_value AS extradhcpopts_1_opt_value, securitygroupportbindings_1.port_id AS securitygroupportbindings_1_port_id, 
    securitygroupportbindings_1.security_group_id AS securitygroupportbindings_1_security_group_id, allowedaddresspairs_1.port_id AS allowedaddresspairs_1_port_id, 
    allowedaddresspairs_1.mac_address AS allowedaddresspairs_1_mac_address, allowedaddresspairs_1.ip_address AS allowedaddresspairs_1_ip_address 
FROM ports 
    LEFT OUTER JOIN ml2_port_bindings ON ports.id = ml2_port_bindings.port_id 
    JOIN ipallocations ON ports.id = ipallocations.port_id 
    LEFT OUTER JOIN ipallocations AS ipallocations_1 ON ports.id = ipallocations_1.port_id 
    LEFT OUTER JOIN ml2_port_bindings AS ml2_port_bindings_1 ON ports.id = ml2_port_bindings_1.port_id 
    LEFT OUTER JOIN ml2_dvr_port_bindings AS ml2_dvr_port_bindings_1 ON ports.id = ml2_dvr_port_bindings_1.port_id 
    LEFT OUTER JOIN extradhcpopts AS extradhcpopts_1 ON ports.id = extradhcpopts_1.port_id 
    LEFT OUTER JOIN securitygroupportbindings AS securitygroupportbindings_1 ON ports.id = securitygroupportbindings_1.port_id 
    LEFT OUTER JOIN allowedaddresspairs AS allowedaddresspairs_1 ON ports.id = allowedaddresspairs_1.port_id 
WHERE ipallocations.subnet_id IN ("35eb4730-3230-4ace-b01b-2faf49a77c61", "4f3649f7-e434-4dc7-872c-edb5844e8f6c", "ae8720de-b61f-4046-9d52-9a167ba97a91", "aab013e4-3da9-4dbf-9080-8ed069fe2164", "f337c1e6-76eb-457b-be87-b0a8e7c10ea9", "50ffd24d-bb79-4699-8bad-06ba470295f3", "cb854358-58bb-4ece-ba02-9b6bacd2339c", "b3867eb4-e903-4649-9442-80bb0209a025", "e8eec67f-37cd-47a2-9d2e-b7647fe26495", "17b6256e-5bbc-4055-a17d-887945ab117d", "a8fdc483-a5c0-4430-b747-0fdb722b9941", "08793e3b-50f0-4a09-8da1-ce3569d4db97", "d22d3cbd-79df-4f41-89da-480f7c7ba785", "d070580f-9d98-46f3-bf7d-b8b1ec8970be", "e9666b0b-c26a-4398-bf00-448c6ba5ecfd", "ae452ac7-c9c5-4e77-9529-fe9d8d924f8c", "896644e1-9922-4c97-ac60-ee5d5192ce52", "dbb64796-38dd-495f-adc4-af70bf41b7ae", "21bced6c-bd53-419a-92eb-13dded30e6d3", "27978544-c3a8-465d-b9e6-cc0d53d61617", "ee79672b-6211-48e1-bb80-930463a31ee7", "25d3ac52-a5b7-40b8-a16d-7556ea2d5514", "ec05e05c-5ad7-4020-95f7-7d270dcb6432", "c4faf7a9-4b7c-4b09-bfba-8e5d55b99770", "ffc10697-96ae-4a50-bbf5-2db15101a7bc", "d0736e7b-ff3c-48dc-9dd9-14401bdde59f", "dca4c082-cf37-4370-b3bf-4a775d315fad", "443b8791-6bc8-498e-8b01-f553f5622ad7", "60d34851-89ed-47d6-bdaf-92c51d363cbf", "fdebdd3c-4928-47bd-8081-d19ba91f6652", "46f0921f-dbea-4c64-bc18-3ec5ca0a0716", "009617d9-c555-4119-b974-11b2f54f3fc7")
'''

'''
filter = {'fixed_ips': {'subnet_id': subnet_ids}}
s = time.time()
for i in range(30):
    ports = core_plugin.get_ports_v2(context, filters=filter)
print 'total time: %s' % (time.time() - s)
'''

'''
session = context.session
bind = session.get_bind()
s = time.time()
for i in range(30):
    start = time.time()
    cur = bind.execute(sql)
    # print '2----excute time: %r sec' % (time.time() - start)

    start = time.time()
    r = cur.fetchall()
    # print r
    # print '3----excute time: %r sec' % (time.time() - start)
print 'total time: %s' % (time.time() - s)
'''


"""
session = context.session
bind = session.get_bind()
s = time.time()
floating_ips = [{'port_id': '0083ad87-9c9b-4711-8242-f4e8fbcf6a70'}, {'port_id': '0283cc9a-1330-4f44-9b96-f49692995038'}, {'port_id': '03936f53-5788-431b-bbdf-fcaca18c7da5'}]
sql = '''
    SELECT 
        ports.id AS port_id, ports.device_id AS device_id, ports.device_owner AS device_owner, 
        ml2_port_bindings.host AS host
    FROM ports 
    LEFT OUTER JOIN ml2_port_bindings ON ports.id = ml2_port_bindings.port_id 
    WHERE id in("%s")
''' % '", "'.join([fip['port_id'] for fip in floating_ips])
cur = bind.execute(sql)
res = cur.fetchall()
print res
print 'total time: %s' % (time.time() - s)
"""


# '''
l3_rpc_callback = l3_rpc.L3RpcCallback()

s = time.time()
# routers = l3_rpc_callback.l3plugin.get_sync_data(context, ['e2321c88-b4b0-4c20-aba3-c041160bbad2'], True)
routers = l3_rpc_callback.sync_routers(context, host='node3')
# pp(routers)
print 'total time: %s' % (time.time() - s)
# '''

#
# run it like this:
# python /home/chao/query.py --config-file /home/chao/neutron.conf --config-file /home/chao/plugin.ini --log-file /home/chao/server.log

