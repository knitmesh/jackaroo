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
    
print "%s: begin to exec release_ip.py" % datetime.datetime.today()
phicloud_conn, phicloud_cursor = get_db_conn(phicloud_db_conf)

neutron_db_conf = copy.copy(phicloud_db_conf)
neutron_db_conf['db'] = 'neutron'

try:
    for region in region_list:
        neutron_db_conf['host']= region['host']
        neutron_db_conf['userName']=region['userName']
        neutron_db_conf['password']=region['password']
        neutron_conn, neutron_cursor = get_db_conn(neutron_db_conf)

        #1 check ip status is going to be assiged, but object id is 0 or null
        print '=======================status: 1============================================='
        last_hour=datetime.datetime.today() - datetime.timedelta(hours=1)
        phicloud_cursor.execute("select INET_NTOA(ip_detail.ip) "
            "from hc_ip_detail ip_detail LEFT JOIN hc_ip_range ip_range on ip_detail.ip_range_id=ip_range.id "
            "LEFT JOIN hc_ip_zone ip_zone on ip_zone.ip_id=ip_range.id LEFT JOIN hc_zone_zonegroup zone_zonegroup "
            "on ip_zone.zone_id=zone_zonegroup.zone_id LEFT JOIN hc_zonegroup zonegroup on "
            "zonegroup.id=zone_zonegroup.zonegroup_id where zonegroup.region_code=%s and ip_detail.`status`=1 "
            "and (ip_detail.object_id=0 or ip_detail.object_id is null or ip_detail.modify_time<%s)",
            [region['region'], last_hour.isoformat()])
        ip_list = phicloud_cursor.fetchall()
        for floating_ip in ip_list:
            if floating_ip[0] is None:
                continue
            neutron_cursor.execute("select ip_address from ipallocations where ip_address=%s", floating_ip[0])
            if not neutron_cursor.fetchall():
                phicloud_cursor.execute("update hc_ip_detail a set a.status=0,a.object_id=0,a.host_id=0 "
                    "where INET_NTOA(a.ip)=%s", floating_ip[0])
                print "%s: update ip_detail ip status value to 0,floating_ip: %s" % (datetime.datetime.today().isoformat(), floating_ip[0])

        #2 check ip status is going to be released 
        print '=======================status: 4============================================='
        phicloud_cursor.execute( "select INET_NTOA(ip_detail.ip) from hc_ip_detail ip_detail LEFT JOIN "
            "hc_ip_range ip_range on ip_detail.ip_range_id=ip_range.id LEFT JOIN hc_ip_zone ip_zone on "
            "ip_zone.ip_id=ip_range.id LEFT JOIN hc_zone_zonegroup zone_zonegroup on "
            "ip_zone.zone_id=zone_zonegroup.zone_id LEFT JOIN hc_zonegroup zonegroup on "
            "zonegroup.id=zone_zonegroup.zonegroup_id where zonegroup.region_code=%s and ip_detail.`status`=4",
            region['region'])
        ip_list=phicloud_cursor.fetchall()
        for floating_ip in ip_list:
            if floating_ip[0] is None:
                continue
            neutron_cursor.execute("select ip_address from ipallocations where ip_address=%s", floating_ip[0])
            if not neutron_cursor.fetchall():
                phicloud_cursor.execute("update hc_ip_detail a set a.status=0,a.object_id=0,"
                    "a.host_id=0 where INET_NTOA(a.ip)=%s", floating_ip[0])
                print "%s: update ip_detail ip status value to 0,floating_ip: %s" % (datetime.datetime.today().isoformat(), floating_ip[0])
        neutron_conn.close()

    #delete hc_vpdc_renewal
    phicloud_cursor.execute("delete hc_vpdc_renewal from hc_vpdc_renewal, hc_vpdc_reference "
        "where hc_vpdc_renewal.referenceId=hc_vpdc_reference.id and hc_vpdc_reference.status=1")

    #update order state
    phicloud_cursor.execute("update hc_order set status=2,remark='订单超过30天未支付，系统自动取消' "
        "where status=0 and TIMESTAMPDIFF(DAY,createDate,SYSDATE())>30")
    print "%s: exec release_ip.py end" % datetime.datetime.today()
    phicloud_conn.commit()
except Exception as e:
    print e
    phicloud_conn.rollback()
finally:
    phicloud_conn.close()
    

