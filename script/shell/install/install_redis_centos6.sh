#!/bin/sh

echo "安装 redis"
echo "判断常见的文件夹是否存在"

if [ ! -d "/opt/setups" ]; then
	mkdir /opt/setups
fi

if [ ! -d "/usr/program" ]; then
	mkdir /usr/program
fi

echo "下载 redis"

cd /opt/setups
wget http://download.redis.io/releases/redis-4.0.6.tar.gz

if [ ! -f "/opt/setups/redis-4.0.6.tar.gz" ]; then
	echo "redis 下载失败，结束脚本"
	exit 1
fi

echo "reids 下载成功"


echo "安装开始"

yum install -y gcc-c++ tcl

cd /opt/setups

tar zxvf redis-4.0.6.tar.gz

if [ ! -d "/opt/setups/redis-4.0.6" ]; then
	echo "redis 解压失败，结束脚本"
	exit 1
fi

mv redis-4.0.6/ /usr/program/

cd /usr/program/redis-4.0.6

make

make install

cp /usr/program/redis-4.0.6/redis.conf /etc/

sed -i 's/daemonize no/daemonize yes/g' /etc/redis.conf

echo "/usr/local/bin/redis-server /etc/redis.conf" >> /etc/rc.local

iptables -I INPUT -p tcp -m tcp --dport 6379 -j ACCEPT

service iptables save

service iptables restart

rm -rf /usr/program/redis-4.0.6

echo "安装结束"
