#!/usr/bin/env python
#encoding: utf-8

import datetime
import MySQLdb
import copy

region_list = [
               {'region': 'regionOne', 'host': '127.0.0.1',"userName":"root","password":"aaaaaa"},
               ]
phicloud_db_conf = {
    'userName': "root",
    'password': "aaaaaa",
    'host': "127.0.0.1",
    'db': 'phicloud_pub',
    'port': 3306,
}


def get_db_conn(db_conf):
    db_conn = MySQLdb.connect(host=db_conf['host'], user=db_conf['userName'],
        passwd=db_conf['password'], db=db_conf['db'], port=db_conf['port'])
    db_cursor = db_conn.cursor()
    return db_conn, db_cursor
    
print "%s: begin to sync state between phicloud and openstack database" % datetime.datetime.today()
phicloud_conn, phicloud_cursor = get_db_conn(phicloud_db_conf)

neutron_db_conf = copy.copy(phicloud_db_conf)
neutron_db_conf['db'] = 'neutron'

try:
    for region in region_list:
        print region['region']
        neutron_db_conf['host']= region['host']
        neutron_db_conf['userName']=region['userName']
        neutron_db_conf['password']=region['password']

        nova_db_conf = copy.copy(neutron_db_conf)
        nova_db_conf['db'] = 'nova'
        neutron_conn, neutron_cursor = get_db_conn(neutron_db_conf)
        nova_conn, nova_cursor = get_db_conn(nova_db_conf)
        try:

            nova_cursor.execute("select LOWER(vm_state), LOWER(task_state), `host`, uuid from instances "
                "where deleted=0")
            nova_instances = nova_cursor.fetchall()
            for instance in nova_instances:
                phicloud_cursor.execute("select LOWER(r.vm_status), LOWER(r.vm_task_status), i.nodeName "
                    "from hc_vpdc_reference r, hc_vpdc_instance i where r.id=i.vpdcrefrenceid "
                    "and i.vm_id=%s", instance[3])
                phicloud_ins = phicloud_cursor.fetchone()
                if phicloud_ins is None:
                    #print 'can\'t find infomation of %s in phicloud database' % instance[3]
                    continue

                instance = list(instance)
                for index in range(len(instance)):
                    if instance[index] is None:
                        instance[index]=''
                instance = tuple(instance)

                inconformity = False 
                for index in range(len(phicloud_ins)):
                    if phicloud_ins[index]!=instance[index]:
                        inconformity = True
                        break

                if inconformity:
                    print ("the instance in nova, uuid is %s, vm_state is %s, task_state is %s, host is %s, but"
                        " in the phicloud, vm_status is %s, vm_task_status is %s, nodeName is %s" %
                        (instance[3], instance[0], instance[1], instance[2],
                        phicloud_ins[0], phicloud_ins[1], phicloud_ins[2]))

                    phicloud_cursor.execute("update hc_vpdc_reference r, hc_vpdc_instance i "
                        "set r.vm_status=UPPER(%s), r.vm_task_status=UPPER(%s), i.nodeName=%s "
                        "where i.vm_id=%s and r.id=i.vpdcrefrenceid", instance)
        finally:
            neutron_conn.close()
            nova_conn.close()

    phicloud_conn.commit()
    print "%s: finish to sync state between phicloud and openstack database" % datetime.datetime.today()
    
except Exception as e:
    print e
    phicloud_conn.rollback()
finally:
    phicloud_conn.close()
    

