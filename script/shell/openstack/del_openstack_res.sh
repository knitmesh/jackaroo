#!/usr/bin/env sh
echo -e "\e[31mbefore you excecute this bash script, please make sure what you are doing"
echo -e "and you may change some of the script\033[00m"
read -p "do you really want to delete whole things of openstack(yes/no)?" choice

if [ "$choice" == "yes" ]; then

    #delete all instance
    nova list|grep -vE 'ID|\+'|cut -d'|' -f2|tr -d ' '|xargs -t -i nova delete {}

    #delete all flavor
    nova flavor-list|grep -vE 'ID|\+'|cut -d'|' -f2|tr -d ' '|xargs -t -i nova flavor-delete {}

    #del all floating ips
    neutron floatingip-list|grep -vE 'id|\+'|cut -d'|' -f2|tr -d ' '|xargs -t -i nova floatingip-delete {}

    #delete subnet, you should know how to ignore the external subnet network
    neutron subnet-list|grep -v ext|grep -vE "id|\+"|cut -d'|' -f2|tr -d ' '|xargs -t -i neutron subnet-delete {}

    #delete network, you should know how to ignore the external network
    neutron net-list|grep -v ext|grep -vE "id|\+"|cut -d'|' -f2|tr -d ' '|xargs -t -i neutron net-delete {}

    #delete secgroup
    nova secgroup-list|grep -vE '(\+|Id|default)'|cut -d'|' -f2|tr -d ' '|xargs -t -i nova secgroup-delete {}

    #delete qos
    neutron qos-list|grep -vE "id|\+"|cut -d'|' -f2|tr -d ' '|xargs -t -i neutron qos-delete {}


    #show all thing after delete
    sleep 10

    echo 'nova list'
    nova list

    echo 'nova flavor-list'
    nova flavor-list

    echo 'neutron subnet-list'
    neutron subnet-list

    echo 'neutron net-list'
    neutron net-list

    echo 'nova secgroup-list'
    nova secgroup-list

    echo 'neutron qos-list'
    neutron qos-list
if

