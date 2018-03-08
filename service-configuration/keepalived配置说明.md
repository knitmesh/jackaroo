### keepalived工作原理和配置说明
#### keepalived的配置文件

keepalived只有一个配置文件keepalived.conf，里面主要包括以下几个配置区域，分别是global_defs、static_ipaddress、static_routes、vrrp_script、vrrp_instance和virtual_server。

##### global_defs区域

主要是配置故障发生时的通知对象以及机器标识

```
global_defs {
    notification_email {
        a@abc.com
        b@abc.com
        ...
    }
    notification_email_from alert@abc.com
    smtp_server smtp.abc.com
    smtp_connect_timeout 30
    enable_traps
    router_id host163
}
```
- notification_email 故障发生时给谁发邮件通知。

- notification_email_from 通知邮件从哪个地址发出。

- smpt_server 通知邮件的smtp地址。

- smtp_connect_timeout 连接smtp服务器的超时时间。

- enable_traps 开启SNMP陷阱（Simple Network Management Protocol）。

- router_id 标识本节点的字条串，通常为hostname，但不一定非得是hostname。故障发生时，邮件通知会用到。


##### static_ipaddress和static_routes区域

static_ipaddress和static_routes区域配置的是是本节点的IP和路由信息。如果你的机器上已经配置了IP和路由，那么这两个区域可以不用配置。其实，一般情况下你的机器都会有IP地址和路由信息的，因此没必要再在这两个区域配置。

```
static_ipaddress {
    10.210.214.163/24 brd 10.210.214.255 dev eth0
    ...
}
static_routes {
    10.0.0.0/8 via 10.210.214.1 dev eth0
    ...
}
```

以上分别表示启动/关闭keepalived时在本机执行的如下命令：

```
# /sbin/ip addr add 10.210.214.163/24 brd 10.210.214.255 dev eth0
# /sbin/ip route add 10.0.0.0/8 via 10.210.214.1 dev eth0
# /sbin/ip addr del 10.210.214.163/24 brd 10.210.214.255 dev eth0
# /sbin/ip route del 10.0.0.0/8 via 10.210.214.1 dev eth0
```
注意： 请忽略这两个区域，因为我坚信你的机器肯定已经配置了IP和路由。

##### vrrp_script区域

用来做健康检查的，当时检查失败时会将vrrp_instance的priority减少相应的值。

```
vrrp_script chk_http_port {
    script "</dev/tcp/127.0.0.1/80"
    interval 1
    weight -10
}
```
以上意思是如果script中的指令执行失败，那么相应的vrrp_instance的优先级会减少10个点。

##### vrrp_instance和vrrp_sync_group区域

vrrp_instance用来定义对外提供服务的VIP区域及其相关属性。

vrrp_rsync_group用来定义vrrp_intance组，使得这个组内成员动作一致。举个例子来说明一下其功能：

两个vrrp_instance同属于一个vrrp_rsync_group，那么其中一个vrrp_instance发生故障切换时，另一个vrrp_instance也会跟着切换（即使这个instance没有发生故障）。

```
vrrp_sync_group VG_1 {
    group {
        inside_network   # name of vrrp_instance (below)
        outside_network  # One for each moveable IP.
        ...
    }
    notify_master /path/to_master.sh
    notify_backup /path/to_backup.sh
    notify_fault "/path/fault.sh VG_1"
    notify /path/notify.sh
    smtp_alert
}
vrrp_instance VI_1 {
    state MASTER
    interface eth0
    use_vmac <VMAC_INTERFACE>
    dont_track_primary
    track_interface {
        eth0
        eth1
    }
    mcast_src_ip <IPADDR>
    lvs_sync_daemon_interface eth1
    garp_master_delay 10
    virtual_router_id 1
    priority 100
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 12345678
    }
    virtual_ipaddress {
        10.210.214.253/24 brd 10.210.214.255 dev eth0
        192.168.1.11/24 brd 192.168.1.255 dev eth1
    }
    virtual_routes {
        172.16.0.0/12 via 10.210.214.1
        192.168.1.0/24 via 192.168.1.1 dev eth1
        default via 202.102.152.1
    }
    track_script {
        chk_http_port
    }
    nopreempt
    preempt_delay 300
    debug
    notify_master <STRING>|<QUOTED-STRING>
    notify_backup <STRING>|<QUOTED-STRING>
    notify_fault <STRING>|<QUOTED-STRING>
    notify <STRING>|<QUOTED-STRING>
    smtp_alert
}
```

- notify_master/backup/fault 分别表示切换为主/备/出错时所执行的脚本。

- notify 表示任何一状态切换时都会调用该脚本，并且该脚本在以上三个脚本执行完成之后进行调用，keepalived会自动传递三个参数（$1 = "GROUP"|"INSTANCE"，$2 = name of group or instance，$3 = target state of transition(MASTER/BACKUP/FAULT)）。

- smtp_alert 表示是否开启邮件通知（用全局区域的邮件设置来发通知）。

- state 可以是MASTER或BACKUP，不过当其他节点keepalived启动时会将priority比较大的节点选举为MASTER，因此该项其实没有实质用途。

- interface 节点固有IP（非VIP）的网卡，用来发VRRP包。

- use_vmac 是否使用VRRP的虚拟MAC地址。

- dont_track_primary 忽略VRRP网卡错误。（默认未设置）

- track_interface 监控以下网卡，如果任何一个不通就会切换到FALT状态。（可选项）

- mcast_src_ip 修改vrrp组播包的源地址，默认源地址为master的IP。（由于是组播，因此即使修改了源地址，该master还是能收到回应的）

- lvs_sync_daemon_interface 绑定lvs syncd的网卡。

- garp_master_delay 当切为主状态后多久更新ARP缓存，默认5秒。

- virtual_router_id 取值在0-255之间，用来区分多个instance的VRRP组播。

注意： 同一网段中virtual_router_id的值不能重复，否则会出错，相关错误信息如下。  

```
Keepalived_vrrp[27120]: ip address associated with VRID not present in received packet :
one or more VIP associated with VRID mismatch actual MASTER advert
bogus VRRP packet received on eth1 !!!
receive an invalid ip number count associated with VRID!
VRRP_Instance(xxx) ignoring received advertisment...
```

可以用这条命令来查看该网络中所存在的vrid：tcpdump -nn -i any net 224.0.0.0/8

- priority 用来选举master的，要成为master，那么这个选项的值最好高于其他机器50个点，该项取值范围是1-255（在此范围之外会被识别成默认值100）。

- advert_int 发VRRP包的时间间隔，即多久进行一次master选举（可以认为是健康查检时间间隔）。

- authentication 认证区域，认证类型有PASS和HA（IPSEC），推荐使用PASS（密码只识别前8位）。

- virtual_ipaddress vip，不解释了。

- virtual_routes 虚拟路由，当IP漂过来之后需要添加的路由信息。

- virtual_ipaddress_excluded 发送的VRRP包里不包含的IP地址，为减少回应VRRP包的个数。在网卡上绑定的IP地址比较多的时候用。

- nopreempt 允许一个priority比较低的节点作为master，即使有priority更高的节点启动。



首先nopreemt必须在state为BACKUP的节点上才生效（因为是BACKUP节点决定是否来成为MASTER的），其次要实现类似于关闭auto failback的功能需要将所有节点的state都设置为BACKUP，或者将master节点的priority设置的比BACKUP低。我个人推荐使用将所有节点的state都设置成BACKUP并且都加上nopreempt选项，这样就完成了关于autofailback功能，当想手动将某节点切换为MASTER时只需去掉该节点的nopreempt选项并且将priority改的比其他节点大，然后重新加载配置文件即可（等MASTER切过来之后再将配置文件改回去再reload一下）。



当使用track_script时可以不用加nopreempt，只需要加上preempt_delay 5，这里的间隔时间要大于vrrp_script中定义的时长。


- preempt_delay master启动多久之后进行接管资源（VIP/Route信息等），并提是没有nopreempt选项。



##### virtual_server_group和virtual_server区域

virtual_server_group一般在超大型的LVS中用到，一般LVS用不过这东西，因此不多说。

```
virtual_server IP Port {
    delay_loop <INT>
    lb_algo rr|wrr|lc|wlc|lblc|sh|dh
    lb_kind NAT|DR|TUN
    persistence_timeout <INT>
    persistence_granularity <NETMASK>
    protocol TCP
    ha_suspend
    virtualhost <STRING>
    alpha
    omega
    quorum <INT>
    hysteresis <INT>
    quorum_up <STRING>|<QUOTED-STRING>
    quorum_down <STRING>|<QUOTED-STRING>
    sorry_server <IPADDR> <PORT>
    real_server <IPADDR> <PORT> {
        weight <INT>
        inhibit_on_failure
        notify_up <STRING>|<QUOTED-STRING>
        notify_down <STRING>|<QUOTED-STRING>
        # HTTP_GET|SSL_GET|TCP_CHECK|SMTP_CHECK|MISC_CHECK
        HTTP_GET|SSL_GET {
            url {
                path <STRING>
                # Digest computed with genhash
                digest <STRING>
                status_code <INT>
            }
            connect_port <PORT>
            connect_timeout <INT>
            nb_get_retry <INT>
            delay_before_retry <INT>
        }
    }
}
```


- delay_loop 延迟轮询时间（单位秒）。

- lb_algo 后端调试算法（load balancing algorithm）。

- lb_kind LVS调度类型NAT/DR/TUN。

- virtualhost 用来给HTTP_GET和SSL_GET配置请求header的。

- sorry_server 当所有real server宕掉时，sorry server顶替。

- real_server 真正提供服务的服务器。

- weight 权重。

- notify_up/down 当real server宕掉或启动时执行的脚本。

- 健康检查的方式，N多种方式。

- path 请求real serserver上的路径。

- digest/status_code 分别表示用genhash算出的结果和http状态码。

- connect_port 健康检查，如果端口通则认为服务器正常。

- connect_timeout,nb_get_retry,delay_before_retry分别表示超时时长、重试次数，下次重试的时间延迟。

其他选项暂时不作说明。



##### keepalived主从切换

主从切换比较让人蛋疼，需要将backup配置文件的priority选项的值调整的比master高50个点，然后reload配置文件就可以切换了。当时你也可以将master的keepalived停止，这样也可以进行主从切换。

keepalived仅做HA时的配置

请看该文档同级目录下的配置文件示例。

说明：
10.210.214.113 为keepalived的备机，其配置文件为113.keepalived.conf
10.210.214.163 为keepalived的主机，其配置文件为163.keepalived.conf
10.210.214.253 为Virtual IP，即提供服务的内网IP地址，在网卡eth0上面
192.168.1.11 为模拟的提供服务的公网IP地址，在网卡eth1上面

用tcpdump命令来捕获的结果如下：


```
17:20:07.919419 IP 10.210.214.163 > 224.0.0.18: VRRPv2, Advertisement, vrid 1, prio 200, authtype simple, intvl 1s, length 20

```

##### LVS+Keepalived配置

注Keepalived与LVS结合使用时一般还会用到一个工具ipvsadm，用来查看相关VS相关状态，关于ipvsadm的用法可以参考man手册。

10.67.15.95为keepalived master，VIP为10.67.15.94，配置文件为95-lvs-keepalived.conf
10.67.15.96为keepalived master，VIP为10.67.15.94，配置文件为96-lvs-keepalived.conf
10.67.15.195为real server

注意：

当使用LVS+DR+Keepalived配置时，需要在real server上添加一条iptables规则（其中dport根据情况添加或缺省）：

```
# iptables -t nat -A PREROUTING -p tcp -d 10.67.15.94 --dport 80 -j REDIRECT
```


当使用LVS+NAT+Keepalived配置时，需要将real server的默认路由配置成Director的VIP10.67.15.94，必须确保client的请求是通过10.67.15.94到达real server的。

##### 安装keepalived

从keepalived官网下载合适的版本，解压并执行如下命令完成安装。

```
# cd keepalived-xxx
# ./configure --bindir=/usr/bin --sbindir=/usr/sbin --sysconfdir=/etc --mandir=/usr/share
# make && make install
```


你也可以打成RPM包，然后安装。

##### 说明

我们用到的HA场景如下： 两台主机host113和host163，内网IP在eth1网卡上，分别是10.210.214.113和10.210.214.163，VIP为公网IP在eth0上，IP地址是202.102.152.253，网关为202.102.152.1。当VIP在host113上提供服务时，host113上的默认路由为202.102.152.1，提供服务的端口为202.102.152.253:443。host113发生故障需要将VIP及服务切回到host163上的时候，需要以下几步，第一将VIP接管过来，第二添加默认路由202.102.152.1，第三启动在端口202.102.152.253:443上的服务。


如此一来，keepalived需要另外的脚本来完成添加默认路由和启动服务工作，这点和heartbeat中的resources是相同的。目前我进行了测试，发现keepalived速度要比heartbeat快，也就是说效率比heartbeat高。并且，最重要的一点，keepalived支持多个backup。


不要问我为何有以上需求。要为两个不同的域名提供https服务，由于SSL证书问题，必须有两个公网IP地址分别绑定443端口。


当然，通过SNI也可以实现一个公网IP绑定443端口来为多个域名提供https服务，但是这需要浏览器支持（M$的IE浏览器不支持）。（nginx/apache）


##### 吐槽

keepalived的主从切换比较让人蛋疼，需要修改配置文件或停止一方的运行。但是由于keepalived是通过vrrp协议来实现failover（故障转移）的，因此也决定了手动主从切换的不便。

keepalived的文档也很旧了，一直都找不到合适的文档，之前我就一直忽略了vrrp_script这个区域，导致很多事情想不通。

另外，我发现我越来越喜欢keepalived了。。。

##### 参考资料


