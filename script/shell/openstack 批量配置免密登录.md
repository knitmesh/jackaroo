添加hosts
```
$ tee -a /etc/hosts <<-"EOF"
172.16.92.12 Ocata1-controller-12
172.16.92.13 Ocata1-controller-13
172.16.92.14 Ocata1-controller-14

172.16.92.15 Ocata1-computer-15
172.16.92.16 Ocata1-computer-16
172.16.92.17 Ocata1-computer-17
172.16.92.18 Ocata1-computer-18
172.16.92.19 Ocata1-computer-19
EOF
```
配置免密码登陆
```
tee ~/.ssh/config <<-EOF
Host 172.16.92.*
   StrictHostKeyChecking no
   UserKnownHostsFile=/dev/null
EOF
```
生成ssh密钥
```
ssh-keygen
```
配置需要免密主机的名和密码的对应列表
```
tee remote-hosts <<-EOF
Ocata1-controller-12:ttcloud@123
Ocata1-controller-13:ttcloud@123
Ocata1-controller-14:ttcloud@123
Ocata1-controller-15:ttcloud@123
Ocata1-controller-16:ttcloud@123
Ocata1-controller-17:ttcloud@123

Ocata1-computer-18:ttcloud@123
Ocata1-computer-19:ttcloud@123
Ocata1-computer-20:ttcloud@123
Ocata1-computer-21:ttcloud@123
Ocata1-computer-22:ttcloud@123
Ocata1-computer-23:ttcloud@123
Ocata1-computer-24:ttcloud@123
Ocata1-computer-25:ttcloud@123
Ocata1-computer-26:ttcloud@123
Ocata1-computer-27:ttcloud@123
Ocata1-computer-28:ttcloud@123
Ocata1-computer-29:ttcloud@123
EOF


```
运行脚本进行批量免密
```
for host in $(cat remote-hosts)
do
       ip=$(echo ${host} | cut -f1 -d ":")
       password=$(echo ${host} | cut -f2 -d ":")
       sshpass -p ${password} ssh-copy-id -o StrictHostKeyChecking=no root@${ip}
done

```

