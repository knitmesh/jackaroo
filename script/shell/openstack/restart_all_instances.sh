#!/usr/bin/env sh
#usage: sh restart_all_instances.sh nodename
#example: sh restart_all_instances.sh node5

if [ $# -lt 3 ] ;then
    echo -e "\e[31merror:\033[00m usage sh restart_all_vm.sh nodename phicloud_databse phicloud_databse_pwd"
else
    echo restart all instances on $1
    vms=`mysql $2 -uroot -p$3 -e "select i.vm_id from hc_vpdc_reference r, hc_vpdc_instance i where r.id=i.vpdcrefrenceid and i.nodeName='$1' and LOWER(r.vm_status)='active' and LOWER(r.vm_task_status)=''"`
    for uuid in $vms; do
        nova list --host $1 --all_tenant|grep $uuid|awk '{print $2}'|xargs -i -t nova reboot {}
    done
fi

