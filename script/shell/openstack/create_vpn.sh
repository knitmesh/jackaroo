#!/bin/bash

# 拨号用户的网段
NETWORK=192.168.189.0/24

# 拨号用户的对端IP
LOCALIP=192.168.189.1

# 拨号用户的允许分配的IP池
REMOTEIP=192.168.189.100-200

#默认分配的用户名和密码
USERNAME=phicloud
PASSWORD=phicloud@123

# 配置用于隧道建立的相关参数
cat >/etc/ppp/options.pptpd <<EOF
name pptpd
require-pap
require-chap
require-mschap
require-mschap-v2
# require-mppe-128
proxyarp
lock
nobsdcomp
novj
novjccomp
nologfd
idle 2592000
ms-dns 8.8.8.8
ms-dns 8.8.4.4
EOF

# 配置拨号用户
cat >/etc/ppp/chap-secrets <<EOF
$USERNAME pptpd $PASSWORD *
EOF

# 配置pptpd，在这里建立拨号用户的IP资源
cat >/etc/pptpd.conf <<EOF
option /etc/ppp/options.pptpd
logwtmp
localip $LOCALIP
remoteip $REMOTEIP
EOF

# 配置防火墙，允许转发
sed -i '/net.ipv4.ip_forward =/d' /etc/sysctl.conf
echo 'net.ipv4.ip_forward = 1' >> /etc/sysctl.conf
/sbin/sysctl -p

# 配置NAT规则，允许拨号用户NAT上网
iptables -t nat -I POSTROUTING -s $NETWORK -j MASQUERADE
service iptables save
service iptables restart

# 重启服务器
service pptpd restart



