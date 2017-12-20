* 批量设置网卡名称

```bash
#!/bin/bash

function modify_settings(){
################################################################
#   set_network     网卡名     ip地址    网关    
#   例:
#   set_network "ifcfg-ens192" "192.168.103.11" "192.168.103.1" 
#
#   为ip地址或网络可以是空 , 为空不设置
#
#   set_network "ifcfg-ens192" "192.168.103.11" ""              
#################################################################
	local BASE_DIR="/etc/sysconfig/network-scripts/"

    local DEFAULT_MASK=255.255.255.0

    set_network "ifcfg-ens192" "192.168.102.11" "192.168.102.1" 
	set_network "ifcfg-ens160" "192.168.103.11" "192.168.103.1" 
	set_network "ifcfg-ens224" "192.168.103.11" "192.168.104.1" 
	set_network "ifcfg-ens256" "192.168.105.11" "192.168.105.1" 
}

function set_network(){
    if [ ! -f "$1" ];then
        echo "文件不存在"
    else
		cd $BASE_DIR
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

function restart_network(){
	if [ ! -n "$1" ] ;then
		echo "准备重启网卡, 确认 'y', 取消'n'"

		read confirm
		if test $confirm = 'y'
		then
			systemctl restart network
		else
			echo "已取消操作"
		fi
	else
		systemctl restart network
	fi
}

modify_settings
restart_network  'f'



```