### 1. 配置需要修改网卡的主机名和密码的对应列表
```bash
tee remote-hosts <<-EOF
kolla-con1:aaaaaa
kolla-con2:aaaaaa
kolla-con3:aaaaaa
kolla-com1:aaaaaa
kolla-com2:aaaaaa
EOF
```

### 2. 创建名为"set_network.sh"的脚本文件, 内容如下

```bash


#!/bin/bash
BASE_DIR="/etc/sysconfig/network-scripts/"
DEFAULT_MASK=255.255.255.0

function set_network(){
    echo $1
    cd $BASE_DIR
    if [ ! -f "$1" ];then
        echo "文件不存在"
    else
        mkdir -p $BASE_DIR/backups
        bak_name=${1}.blk_`date +%m%d%H%M%S`
        cp ./$1 ./backups/$bak_name
        # 删除IPV6相关配置
        sed  -i '/^IPV6/d' $1
        # 设置开机自启
        sed -ri 's/(ONBOOT=).*/\1yes/' ./$1
        # 设置固定ip
        sed -ri 's/(BOOTPROTO=).*/\1static/' ./$1
        # 设置ip地址
        if [ -n "$2" ] ;then
            if grep -q "IPADDR" $1 ;then
                sed -ri "s/(IPADDR=).*/\1${2}/" ./$1
            else
                echo IPADDR=$2 >> ./$1
            fi
        fi
        # 设置网关
        if [ -n "$3" ] ;then
            if grep -q "GATEWAY" $1 ;then
                sed -ri "s/(GATEWAY=).*/\1${3}/" ./$1
            else
                echo GATEWAY=$3 >> ./$1
            fi
        fi
        # 设置MASK地址
        if grep -q "NETMASK" $1 ;then
            sed -ri "s/(NETMASK=).*/\1${DEFAULT_MASK}/" ./$1
        else
            echo NETMASK=$DEFAULT_MASK >> ./$1
        fi
        echo "${1}已备份至 --> ${BASE_DIR}/backups/${bak_name}"
        echo " "
        echo "请确认${1}变更是否正确"
        egrep -C 1000 "GATEWAY|IPADDR|NETMASK|ONBOOT|BOOTPROTO" ./$1 --color=auto
        echo ""
    fi
}

set_network $1 $2 $3


```

## 3. 创建名为"modify_net_setting.sh"的脚本文件, 内容如下

```bash


#!/bin/bash

for host in $(cat remote-hosts)
        do
        if [[ ! "$host" =~ ^# ]];
        then

        
        ip=$(echo ${host} | awk -F '[-:]' '{print $3}')
        hostname=$(echo ${host} | cut -f1 -d ":")
        echo ${hostname}
        password=$(echo ${host} | cut -f2 -d ":")


        sshpass -p ${password} ssh "root@${hostname}" 'bash -s' < set_networks.sh "ifcfg-ens192" "10.120.200.${ip}" ""
        sshpass -p ${password} ssh "root@${hostname}" 'bash -s' < set_networks.sh "ifcfg-ens224" "10.10.100.${ip}" ""
        sshpass -p ${password} ssh "root@${hostname}" 'bash -s' < set_networks.sh "ifcfg-ens256" "10.10.200.${ip}" ""

        # restart
        echo "重启网卡"
        sshpass -p ${password} ssh "root@${hostname}" 'systemctl restart network'
        sshpass -p ${password} ssh "root@${hostname}" 'ip a'
        fi
        done


```

### 4. 执行脚本

```
bash modify_net_setting.sh
```