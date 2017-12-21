## CentOS 自动生成网卡配置文件

### 文件
* mv-net-cfg -- 开机后迁移配置文件的脚本
* write_net_rules -- 增加生成网卡配置文件后的write_net_rules
* install.sh -- 安装脚本

### 使用
将三个文件放入同一目录下，执行install.sh 即可

### 效果
在CentOS系统中，当插入新的网卡的时候，会自动生成网卡配置文件
如果在关机后加入的网卡，则会将配置文件生成在临时目录，等系统开机后由mv-net-cfg完成配置
文件迁回工作
