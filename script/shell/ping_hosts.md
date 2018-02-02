```bash
tee remote-hosts <<-EOF
develop-r1-controller-2:openstack@123:172.16.92.2
develop-r1-controller-3:openstack@123:172.16.92.3
develop-r1-controller-4:openstack@123:172.16.92.4
develop-r1-computer-5:openstack@123:172.16.92.5
develop-r1-computer-6:openstack@123:172.16.92.6
develop-r1-computer-7:openstack@123:172.16.92.7
develop-r1-computer-8:openstack@123:172.16.92.8
develop-r1-computer-9:openstack@123:172.16.92.9
EOF

```



```bash

function ping_hosts(){
        # ping 1次 检查掉包率, 如果是100就是全部掉包，那就是没ping通
        ping=`ping -c 1 $1|grep loss|awk '{print $6}'|awk -F "%" '{print $1}'`
        if [ $ping -eq 100  ];then
            echo ping $1 fail
        else
            echo ping $1 ok
        fi
}


for host in $(cat remote-hosts)
        do
    
        ip=$(echo ${host} | awk -F '[:]' '{print $1}' | awk -F '[-]' '{print $NF}')
        ping_hosts "10.120.200.${ip}"
        ping_hosts "10.10.100.${ip}"
        ping_hosts "10.10.200.${ip}"
        ping_hosts "172.16.92.${ip}"
        
        done


```