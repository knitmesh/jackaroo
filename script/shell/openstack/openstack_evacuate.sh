#/bin/bash
check_evacuate_error(){
    DB_PWD=69ba78f1
    check_list=$1
    source_host=$2
    echo 'begin to check the all instance evacuate list...'
    reboot_ins=''
    while [ 1 ]; do
        need_recheck='false'
        for instance in $check_list; do
            info=`mysql nova -unova -p$DB_PWD -e "select task_state, vm_state, host from instances where uuid=\"$instance\""| grep -vP 'task_state|\+'`
            task_state=`echo $info | awk '{print $1}'`
            vm_state=`echo $info | awk '{print $2}'`
            host=`echo $info | awk '{print $3}'`

            # maybe evacuate nothing
            if [[ "$source_host" == "$host" ]];then
                continue
            fi

            # has reboot, in this cycle, do nothing
            has_rebooted=`echo $reboot_ins | grep $instance`
            if [[ '' != $has_rebooted ]];then
                echo "4: $instance has rebooted, omit it"
                continue
            fi

            # check rebuild error
            if [[ 'error' == "$vm_state" ]];then
                echo "1: $instance error, update db"
                mysql nova -unova -p$DB_PWD -e "update instances set vm_state='active', task_state=null, power_state=1 where uuid=\"$instance\""
                nova reboot $instance --hard
                reboot_ins= "${reboot_ins} ${instance}"
                need_recheck='true'
            elif [[ 'NULL'  != "$task_state" ]];then
                echo "2: $instance tasking $task_state"
                need_recheck='true'
            else
                echo "3: $instance state $task_state | $vm_state"
            fi
        done
        if [[ 'false' ==  "$need_recheck" ]];then
            exit 0
        fi
        sleep 3
    done
}

hypervisors=`nova hypervisor-list|awk '{print $4}'`
pid=`echo $$`
process=`ps -ef | grep 'sh /usr/local/bin/openstack_evacuate' | grep -v $pid | grep -v grep`
if [[ '' != $process ]];then
    echo "openstack_evacuate has exist, exit"
    exit 0
fi

for hyper in $hypervisors; do
    echo "test $hyper"
    hyper_ip=`nova hypervisor-show $hyper|grep host_ip|awk '{print $4}'`
    
    if [ -z "$hyper_ip" ]; then
        continue
    fi

    ping $hyper_ip -c 3 -i 1|grep '100% packet loss' 
    if [ $? -ne 0 ]; then
        echo "test 3s, $hyper is alive"
        continue
    fi

    ping $hyper_ip -c 7 -i 1|grep '100% packet loss' 
    if [ $? -eq 0 ]; then
        echo "nova host-evacuate $hyper --on-shared-storage"
        evacuate_list=`nova host-evacuate $hyper --on-shared-storage | grep -vP '^\+|Server UUID'`
        printf "${evacuate_list}\n"

        need_check_uuids=''
        evacuate_uuids=`printf "$evacuate_info" | awk -F '|' '{print $2}'`
        evacuate_status=`printf "$evacuate_info" | awk -F '|' '{print $3}'`
        evacuate_uuids=(${evacuate_uuids// / })
        evacuate_status=(${evacuate_status// / })
        n=${#evacuate_uuids[*]}
        for ((i=0; i<n; i++)); do
            s=`echo ${evacuate_status[$i]}`
            if [[ 'True' == "$s" ]];then
                need_check_uuids="${need_check_uuids} ${evacuate_uuids[$i]}"
            else
                echo "evacuate ${evacuate_uuids[$i]} fail, reason: ${evacuate_status[$i]}"
            fi
        done
        if [[ '' == "$need_check_uuids" ]];then
            echo "do not need check for host ${hyper} evacuate, next"
            continue
        fi
        echo "wait for evacuate host vms, 120s..."
        sleep 120
        check_evacuate_error "$need_check_uuids" "$hyper"
    else
        echo "test 7s, $hyper is alive"
    fi
done


