## MySQL5.6简介

``` 
MySQL 5.5发布两年后，Oracle宣布MySQL 5.6正式版发布，首个正式版版本号为5.6.10。在MySQL 5.5中使用的是InnoDB作为默认的存储引擎，而MySQL 5.6则对InnoDB引擎进行了改造，提供全文索引能力，使InnoDB适合各种应用场景。
About MySQL 5.6
InnoDB 性能改进
在线DDL更改
对InnoDB 进行NoSQL访问 
```

## MySQL 5.6 功能：

**MySQL 5.6 同时引入了 NoSQL 接口，提供了兼容 memcached 的 API，该特性让应用可直接访问 InnoDB 存储引擎。底层上保持着跟关系数据库引擎在维护上的统一。同时底层的 InnoDB 引擎也增强在持久化优化统计、多线程消除以及提供更多的系统表和监控数据。**

MySQL 的产品经理 Tomas Ulin 解释了开源社区对 Oracle 关于补丁政策的批评，他说：这是一个不断求证的过程，我们每三个月提供安全补丁，但其实大多数用户并不会这么频繁的更新。而使用社区版的用户抱怨 Oracle 没有提供发行说明中 CVE 号的详细说明，它们只是简单的指向 Oracle 内部的错误码。公司将不会发布这些详情信息。

##MySQL 5.6 显著提高了性能和可用性，可支持下一代 Web、嵌入式和云计算应用程序。

```
MySQL Database 5.6 具备以下特性：

新增！ 在线 DDL /更改数据架构支持动态应用程序和开发人员灵活性

新增！ 复制全局事务标识可支持自我修复式集群

新增！ 复制无崩溃从机可提高可用性

新增！ 复制多线程从机可提高性能

新增！ 对 InnoDB 进行 NoSQL 访问，可快速完成键值操作以及快速提取数据来完成大数据部署

改进！ 在 Linux 上的性能提升多达 230%

改进！ 在当今、多核、多 CPU 硬件上具备更高的扩展力

改进！ InnoDB 性能改进，可更加高效地处理事务和只读负载

改进！ 更快速地执行查询，增强的诊断功能

改进！ Performance Schema 可监视各个用户/应用程序的资源占用情况

改进！ 通过基于策略的密码管理和实施来确保安全性

高度可靠，几乎无需干预即可确保系统持续不间断运行

简便易用，只需 3 分钟即可完成从下载到开发环境的安装和配置过程

管理需求低，数据库维护工作非常少

复制功能 支持灵活的拓扑架构，可实现向外扩展和高可用性

分区 有助于提高性能和管理超大型数据库环境

ACID 事务 支持构建安全可靠的关键业务应用程序

存储过程 可提高开发人员效率
```

## RPM安装5.6 

```
RPM方式安装MySQL5.6

可关注参考 https://github.com/yangcvo/MySQL5.6

a. 检查MySQL及相关RPM包，是否安装，如果有安装，则移除（rpm –e 名称）
     
     [root@localhost ~]# rpm -qa | grep -i mysql mysql-libs-5.1.66-2.el6_3.x86_64
     [root@localhost ~]# yum -y remove mysql-libs*

b. 下载Linux对应的RPM包，如：CentOS6.4_64对应的RPM包，如下：

    [root@localhost rpm]# ll
    total 74364
    -rw-r--r--. 1 root root 18442536 Dec 11 20:19 MySQL-client-5.6.15-1.el6.x86_64.rpm
    -rw-r--r--. 1 root root  3340660 Dec 11 20:06 MySQL-devel-5.6.15-1.el6.x86_64.rpm
    -rw-r--r--. 1 root root 54360600 Dec 11 20:03 MySQL-server-5.6.15-1.el6.x86_64.rpm

c. 安装MySQL

    [root@localhost rpm]# rpm -ivh MySQL-server-5.6.15-1.el6.x86_64.rpm
 如果安装不上，--nodeps —force 强制安装

    [root@localhost rpm]# rpm -ivh MySQL-devel-5.6.15-1.el6.x86_64.rpm

    [root@localhost rpm]# rpm -ivh MySQL-client-5.6.15-1.el6.x86_64.rpm

修改配置文件位置
 
    [root@localhost rpm]# cp /usr/share/mysql/my-default.cnf /etc/my.cnf

d. 初始化MySQL及设置密码

    [root@localhost rpm]# /usr/bin/mysql_install_db

    [root@localhost rpm]# service mysql start

     [root@localhost rpm]# cat /root/.mysql_secret  #查看root账号密码

    # The random password set for the root user at Wed Dec 11 23:32:50 2013 (local time): qKTaFZnl

    [root@localhost ~]# mysql -uroot –pqKTaFZnl

     mysql> SET PASSWORD = PASSWORD('123456');    #设置密码为123456

     mysql> exit
     [root@localhost ~]# mysql -uroot -p123456

e. 允许远程登陆


    mysql> use mysql;

    mysql> select host,user,password from user;

     +-----------------------+------+------------------------------------------+

    | host     | user | password                                               |

    +-----------------------+------+-------------------------------------------+

    | localhost             | root | *6BB4837EB74329105EE4568DDA7DC67ED2CA2AD9 |

    | localhost.localdomain | root | *1237E2CE819C427B0D8174456DD83C47480D37E8 |


    | 127.0.0.1             | root | *1237E2CE819C427B0D8174456DD83C47480D37E8 |


    | ::1                   | root | *1237E2CE819C427B0D8174456DD83C47480D37E8 |

    +-----------------------+------+-------------------------------------------+


    mysql> update user set password=password('123456') where user='root';
    设置MySQL新密码
    mysql> update user set host='%' where user='root' and host='localhost';
    设置所有IP都可以root登陆。这里我不建议这么做，只做内网网段访问即可。
    mysql> flush privileges;
    刷新权限。
    mysql> exit
     退出
f. 设置开机自启动


    [root@localhost ~]# chkconfig mysql on

    [root@localhost ~]# chkconfig --list | grep mysql
    mysql           0:off   1:off   2:on    3:on    4:on    5:on    6:off

g. MySQL的默认安装位置

     /var/lib/mysql/               #数据库目录
     /usr/share/mysql              #配置文件目录
     /usr/bin                     #相关命令目录
     /etc/init.d/mysql              #启动脚本

修改字符集和数据存储路径


配置/etc/my.cnf文件,修改数据存放路径、mysql.sock路径以及默认编码utf-8.

这里对/etc/my.cnf做了简单的配置。后期会讲my.cnf 配置优化
   [html] view plain copy

- [client]
- password        = 123456
- port            = 3306
- default-character-set=utf8
- [mysqld]
- port            = 3306
- character_set_server=utf8
- character_set_client=utf8
- collation-server=utf8_general_ci
- #(注意linux下mysql安装完后是默认：表名区分大小写，列名不区分大小写； 0：区分大小写，1：不区分大小写)
- lower_case_table_names=1
- #(设置最大连接数，默认为 151，MySQL服务器允许的最大连接数16384; )
- max_connections=1000
- [mysql]
- default-character-set = utf8

查看字符集

    show variables like '%collation%';

    show variables like '%char%';

初始化成功： 下面的初始化内容

登录进去报错。

这个报错事说密码没有设置。
那我设置下密码看下。
可使用SET PASSWORD命令修改root用户的密码，参考如下：

    mysql> SET PASSWORD = PASSWORD('root123456');
    Query OK, 0 rows affected (0.00 sec)
    mysql> show databases;
    +--------------------+
    | Database           |
    +--------------------+
    | information_schema |
    | mysql              |
    | performance_schema |
    | test               |
    +--------------------+
```

## my.cnf 优化配置
```
[mysqld]
#mysqld程序--目录和文件
user=mysql                          # MySQL启动用户：mysql
datadir = /var/lib/mysql            #  使用给定目录作为根目录(安装目录)。
server-id = 1                       #表示是本机的序号为1,一般来讲就是master的意思
pid-file=/opt/mysql/log/mysql.pid    #为mysqld程序指定一个存放进程ID的文件(仅适用于UNIX/Linux系统);

socket=/var/lib/mysql/mysql.sock   # 为MySQL客户程序与服务器之间的本地通信指定一个套接字文件(Linux下默认是/var/lib/mysql/mysql.sock文件)
port  = 3306                       # 指定MsSQL侦听的端口
key_buffer       = 384M            # key_buffer_size指定用于索引的缓冲区大小，增加它可得到更好处理的索引(对所有读和多重写)，到你能负担得起那样多。如果你使它太大，系统将开始换页并且真的变慢了。对于内存在4GB左右的服务器该参数可设置为384M或512M。通过检查状态值Key_read_requests和 Key_reads,可以知道key_buffer_size设置是否合理。比例key_reads / key_read_requests应该尽可能的低，至少是1:100，1:1000更好(上述状态值可以使用SHOW STATUS LIKE ‘key_read%'获得)。注意：该参数值设置的过大反而会是服务器整体效率降低!
table_cache = 512                  #table_cache指定表高速缓存的大小。每当MySQL访问一个表时，如果在表缓冲区中还有空间，该表就被打开并放入其中，这样可以更快地访问表内容。通过检查峰值时间的状态值Open_tables和Opened_tables，可以决定>是否需要增加table_cache的值。如果你发现 open_tables等于table_cache，并且opened_tables在不断增长，那么你就需要增加table_cache的值了(上述状态值可以使用SHOW STATUS LIKE ‘Open%tables'获得)。注意，不能盲目地把table_cache设置成很大的值。如果设置得太高，可能会造成文件描述符不足，从而造成性能不稳定或者连接失败。


default-storage-engine=InnoDB      #innodb为默认数据引擎
max_connections = 1000000          #指定MySQL允许的最大连接进程数。如果在访问论坛时经常出现Too Many Connections的错误提示，则需要增大该参数值。
max_connect_errors = 100000000     #对于同一主机，如果有超出该参数值个数的中断错误连接，则该主机将被禁止连接。如需对该主机进行解禁，执行：FLUSH HOST;。
wait_timeout = 8                   #指定一个请求的最大连接时间，对于4GB左右内存的服务器可以设置为5-10。
thread_concurrency = 8             #该参数取值为服务器逻辑CPU数量×2，在本例中，服务器有2颗物理CPU，而每颗物理CPU又支持H.T超线程，所以实际取值为4 × 2 = 8

long_query_time = 1                #慢查询时间 超过1秒则为慢查询
binlog_checksum=NONE               #默认为NONE， 表示在图1的箭头1 不生成checksum， 这样就可以兼容旧版本的mysql。此外，就只能设置为CRC32了。
binlog_format=MIXED                #设定主从复制模式的方法非常简单，只要在以前设定复制配置的基础上，再加一个参数：以上两种模式的混合使用，一般的复制使用STATEMENT模式保存binlog，对于STATEMENT模式无法复制的操作使用ROW模式保存binlog，MySQL会根据执行的SQL语句选择日志保存方式。
log-bin                 = /opt/mysql/mysql-bin.log    # binlog日志文件
expire_logs_days        = 7                           # binlog过期清理时间
max_binlog_size         = 100m                        # binlog每个日志文件大小
binlog_cache_size       = 4m                          # binlog缓存大小
max_binlog_cache_size   = 512m                        # 最大binlog缓存大小

symbolic-links=0
log_bin=mysql-bin
character_set_server=utf8                            #写入的数据到MySQL中数据为防止中文乱码。
lower_case_table_names=1                             #这样MySQL 将在创建与查找时将所有的表名自动转换为小写字符
character_set_client=utf8                            #集群写入的数据到MySQL中数据为防止中文乱码。
collation-server=utf8_general_ci

sql_mode=NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES

[mysqld_safe]
log-error=/opt/mysql/log/error.log   #错误日志路径
log=/opt/mysql/log/mysql.log         #普通日志路径
master-port=3306                     #mysql master端口
log-slave-updates

[mysql]

[client]
default-character-set=utf8
socket=/var/lib/mysql/mysql.sock
```

##MySQL主从详细配置

**Mysql作为目前世界上使用最广泛的免费数据库，相信所有从事系统运维的工程师都一定接触过。但在实际的生产环境中，由单台Mysql作为独立的数据库是完全不能满足实际需求的，无论是在安全性，高可用性以及高并发等各个方面。**

**因此，一般来说都是通过 主从复制（Master-Slave）的方式来同步数据，再通过读写分离（MySQL-Proxy）来提升数据库的并发负载能力 这样的方案来进行部署与实施的。**

```
MySQL主从同步的作用 
1、可以作为一种备份机制，相当于热备份 
2、可以用来做读写分离，均衡数据库负载 
MySQL主从同步的步骤 
一、准备操作 
1、主从数据库版本一致，建议版本5.5以上 
2、主从数据库数据一致 

```
如下图所示：
![image](http://7xrthw.com1.z0.glb.clouddn.com/githubmysql-master-slave-proxy.jpg) 
```
下面是我在实际工作过程中所整理的笔记，在此分享出来，以供大家参考。

一、MySQL的安装与配置
具体的安装过程，建议参考我的这一篇文章：
值得一提的是，我的安装过程都是源码包编译安装的，并且所有的配置与数据等都统一规划到了/opt/mysql目录中，因此在一台服务器上安装完成以后，可以将整个mysql目录打包，然后传到其它服务器上解包，便可立即使用。
二、MySQL主从复制
场景描述：
主数据库服务器：192.168.1.170，MySQL已经安装，并且无应用数据。
从数据库服务器：192.168.1.171，MySQL已经安装，并且无应用数据。

2.1 主服务器上进行的操作
启动mysql服务

root@mysql mysql]# /etc/init.d/mysql start
Starting MySQL. SUCCESS!

打开主机A的my.cnf，输入

log_bin=/var/lib/mysql/mysql-bin.log    #确保此文件可写
read-only       =0  #主机，读写都可以
binlog-do-db    =yjk   #需要备份数据，多个写多行
binlog-ignore-db=mysql #不需要备份的数据库，多个写多行
character_set_server=utf8                            #写入的数据到MySQL中数据为防止中文乱码。
lower_case_table_names=1                             #这样MySQL 将在创建与查找时将所有的表名自动转换为小写字符
character_set_client=utf8                            #集群写入的数据到MySQL中数据为防止中文乱码。
collation-server=utf8_general_ci

这里我在之前新创建了个数据库。我需要备份的数据。

打开从机B的my.cnf，输入

server-id               = 2
log_bin                 = /var/lib/mysql/mysql-bin.log
master-host     =192.168.1.170
master-user     =backup
master-pass     =backup
master-port     =3306
master-connect-retry=60 #如果从服务器发现主服务器断掉，重新连接的时间差(秒)
replicate-do-db =test #只复制某个库
replicate-ignore-db=mysql #不复制某个库

配置好保存。

同步数据库
有多种方法，我说最笨的一种，先mysqldump导出主机A的数据yjk为 yjk.sql
然后在，从机B上建立数据库yjk，mysql导入 yjk.sql到yjk库中
先重启主机A的mysql，再重启从机B的mysql

通过命令行登录管理MySQL服务器
[root@mysql mysql]# mysql -uroot -p’new-password'
Warning: Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 1
Server version: 5.6.20-log MySQL Community Server (GPL)

Copyright (c) 2000, 2014, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.


授权给从数据库服务器192.168.11.171

mysql> GRANT REPLICATION SLAVE ON *.*to 'backup'@'192.168.1.171' identified by 'backup';
Query OK, 0 rows affected (0.02 sec)

查询主数据库状态

1 row in set (0.00 sec)

mysql>  show master status;
+------------------+----------+--------------+------------------+-------------------+
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+------------------+----------+--------------+------------------+-------------------+
| mysql-bin.000008 |      120 | yjk          | mysql            |                   |
+------------------+----------+--------------+------------------+-------------------+
1 row in set (0.00 sec)

mysql>  show master status\G;
*************************** 1. row ***************************
             File: mysql-bin.000008
         Position: 120
     Binlog_Do_DB: yjk
 Binlog_Ignore_DB: mysql
Executed_Gtid_Set:
1 row in set (0.00 sec)



记录下 FILE 及 Position 的值，在后面进行从服务器操作的时候需要用到。




2.2 配置从服务器
修改从服务器的配置文件/etc/my.cnf
将 server-id = 1修改为 server-id = 10，并确保这个ID没有被别的MySQL服务所使用。
启动mysql服务
[root@mysql-2 log]# /etc/init.d/mysql stop
Shutting down MySQL.. SUCCESS!
[root@mysql-2 log]# /etc/init.d/mysql start
Starting MySQL. SUCCESS!

通过命令行登录管理MySQL服务器
bin/mysql -uroot -p'new-password'

执行同步SQL语句
mysql> change master to
master_host=’192.168.10.130’,
master_user=’rep1’,
master_password=’password’,
master_log_file=’mysql-bin.000005’,
master_log_pos=261;

这里同步数据：提示我报错；ERROR 1198 (HY000): This operation cannot be performed with a running slave; run STOP SLAVE first

网上看了MySQL配置master/slave常用命令，需要先停止slave。 

mysql> show slave status;
+----------------+---------------+-------------+-------------+---------------+-----------------+---------------------+------------------------+---------------+-----------------------+------------------+-------------------+-----------------+---------------------+--------------------+------------------------+-------------------------+-----------------------------+------------+------------+--------------+---------------------+-----------------+-----------------+----------------+---------------+--------------------+--------------------+--------------------+-----------------+-------------------+----------------+-----------------------+-------------------------------+---------------+-----------------------------------------------------------------------------------------------------------------------+----------------+----------------+-----------------------------+------------------+-------------+----------------------------+-----------+---------------------+-----------------------------------------------------------------------------+--------------------+-------------+-------------------------+--------------------------+----------------+--------------------+--------------------+-------------------+---------------+
| Slave_IO_State | Master_Host   | Master_User | Master_Port | Connect_Retry | Master_Log_File | Read_Master_Log_Pos | Relay_Log_File         | Relay_Log_Pos | Relay_Master_Log_File | Slave_IO_Running | Slave_SQL_Running | Replicate_Do_DB | Replicate_Ignore_DB | Replicate_Do_Table | Replicate_Ignore_Table | Replicate_Wild_Do_Table | Replicate_Wild_Ignore_Table | Last_Errno | Last_Error | Skip_Counter | Exec_Master_Log_Pos | Relay_Log_Space | Until_Condition | Until_Log_File | Until_Log_Pos | Master_SSL_Allowed | Master_SSL_CA_File | Master_SSL_CA_Path | Master_SSL_Cert | Master_SSL_Cipher | Master_SSL_Key | Seconds_Behind_Master | Master_SSL_Verify_Server_Cert | Last_IO_Errno | Last_IO_Error                                                                                                         | Last_SQL_Errno | Last_SQL_Error | Replicate_Ignore_Server_Ids | Master_Server_Id | Master_UUID | Master_Info_File           | SQL_Delay | SQL_Remaining_Delay | Slave_SQL_Running_State                                                     | Master_Retry_Count | Master_Bind | Last_IO_Error_Timestamp | Last_SQL_Error_Timestamp | Master_SSL_Crl | Master_SSL_Crlpath | Retrieved_Gtid_Set | Executed_Gtid_Set | Auto_Position |
+----------------+---------------+-------------+-------------+---------------+-----------------+---------------------+------------------------+---------------+-----------------------+------------------+-------------------+-----------------+---------------------+--------------------+------------------------+-------------------------+-----------------------------+------------+------------+--------------+---------------------+-----------------+-----------------+----------------+---------------+--------------------+--------------------+--------------------+-----------------+-------------------+----------------+-----------------------+-------------------------------+---------------+-----------------------------------------------------------------------------------------------------------------------+----------------+----------------+-----------------------------+------------------+-------------+----------------------------+-----------+---------------------+-----------------------------------------------------------------------------+--------------------+-------------+-------------------------+--------------------------+----------------+--------------------+--------------------+-------------------+---------------+
|                | 192.168.1.170 |             |        3306 |            60 |                 |                   4 | mysql-relay-bin.000005 |             4 |                       | No               | Yes               |                 |                     |                    |                        |                         |                             |          0 |            |            0 |                 120 |             120 | None            |                |             0 | No                 |                    |                    |                 |                   |                |                     0 | No                            |          1593 | Fatal error: Invalid (empty) username when attempting to connect to the master server. Connection attempt terminated. |              0 |                |                             |                0 |             | /var/lib/mysql/master.info |         0 |                NULL | Slave has read all relay log; waiting for the slave I/O thread to update it |              86400 |             | 160612 16:56:22         |                          |                |                    |                    |                   |             0 |
+----------------+---------------+-------------+-------------+---------------+-----------------+---------------------+------------------------+---------------+-----------------------+------------------+-------------------+-----------------+---------------------+--------------------+------------------------+-------------------------+-----------------------------+------------+------------+--------------+---------------------+-----------------+-----------------+----------------+---------------+--------------------+--------------------+--------------------+-----------------+-------------------+----------------+-----------------------+-------------------------------+---------------+-----------------------------------------------------------------------------------------------------------------------+----------------+----------------+-----------------------------+------------------+-------------+----------------------------+-----------+---------------------+-----------------------------------------------------------------------------+--------------------+-------------+-------------------------+--------------------------+----------------+--------------------+--------------------+-------------------+---------------+
1 row in set (0.00 sec)

这里我看到了我之前配置的，所以在配置就会报刚才那个错误。

这里只需
mysql> stop slave;
Query OK, 0 rows affected (0.01 sec)


mysql> change master to
    -> master_host='192.168.1.170',
    -> master_user='backup',
    -> master_password='backup',
    -> master_log_file='mysql-bin.000008',
    -> master_log_pos=120;
Query OK, 0 rows affected, 2 warnings (0.01 sec)



正确执行后启动Slave同步进程
mysql> start slave;

mysql> start slave;
Query OK, 0 rows affected (0.01 sec)



主从同步检查：



从：
mysql> show slave status\G
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: 192.168.1.170
                  Master_User: backup
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: mysql-bin.000008
          Read_Master_Log_Pos: 120
               Relay_Log_File: mysql-relay-bin.000002
                Relay_Log_Pos: 279
        Relay_Master_Log_File: mysql-bin.000008
             Slave_IO_Running: Yes
            Slave_SQL_Running: Yes
              Replicate_Do_DB:
          Replicate_Ignore_DB:
           Replicate_Do_Table:
       Replicate_Ignore_Table:
      Replicate_Wild_Do_Table:
  Replicate_Wild_Ignore_Table:
                   Last_Errno: 0
                   Last_Error:
                 Skip_Counter: 0
          Exec_Master_Log_Pos: 120
              Relay_Log_Space: 448
              Until_Condition: None
               Until_Log_File:
                Until_Log_Pos: 0
           Master_SSL_Allowed: No
           Master_SSL_CA_File:
           Master_SSL_CA_Path:
              Master_SSL_Cert:
            Master_SSL_Cipher:
               Master_SSL_Key:
        Seconds_Behind_Master: 0
Master_SSL_Verify_Server_Cert: No
                Last_IO_Errno: 0
                Last_IO_Error:
               Last_SQL_Errno: 0
               Last_SQL_Error:
  Replicate_Ignore_Server_Ids:
             Master_Server_Id: 1
                  Master_UUID: d4280377-2fe1-11e6-8b17-525400e91531
             Master_Info_File: /var/lib/mysql/master.info
                    SQL_Delay: 0
          SQL_Remaining_Delay: NULL
      Slave_SQL_Running_State: Slave has read all relay log; waiting for the slave I/O thread to update it
           Master_Retry_Count: 86400
                  Master_Bind:
      Last_IO_Error_Timestamp:
     Last_SQL_Error_Timestamp:
               Master_SSL_Crl:
           Master_SSL_Crlpath:
           Retrieved_Gtid_Set:
            Executed_Gtid_Set:
                Auto_Position: 0
1 row in set (0.00 sec)

其中Slave_IO_Running 与 Slave_SQL_Running 的值都必须为YES，才表明状态正常。即YES状态，否则说明同步失败。 
到这里，主从数据库设置工作已经完成，自己可以新建数据库和表，插入和修改数据，测试一下是否成功 .


如果主服务器已经存在应用数据，则在进行主从复制时，需要做以下处理：
(1)主数据库进行锁表操作，不让数据再进行写入动作
mysql> FLUSH TABLES WITH READ LOCK;

(2)查看主数据库状态
mysql> show master status;


//显示所有本机上的二进制日志
mysql> SHOW MASTER LOGS;

(3)复制数据文件
将主服务器的数据文件（整个/var/lib/mysql 目录）复制到从服务器，建议通过tar归档压缩后再传到从服务器解压。  这里我写的目录是/var/lib/mysql 下面的 建议配置my.cnf 设置个/opt/mysql/data  单独存放数据。


4)取消主数据库锁定
mysql> UNLOCK TABLES;


2.3 验证主从复制效果

主服务器上的操作

在主服务器上的表first_tb中插入记录
mysql> insert into first_tb values (001,’myself’);
Query Ok, 1 row affected (0.00 sec)



在从服务器上查看

[root@mysql-2 mysql]# mysql -uroot -pIhaozhuo_b313
Warning: Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 8
Server version: 5.6.20-log MySQL Community Server (GPL)

Copyright (c) 2000, 2014, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| data               |
| mysql              |
| performance_schema |
| test               |
| yjk                |
+--------------------+
6 rows in set (0.00 sec)

mysql> use yjk;
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> show tables;
+---------------+
| Tables_in_yjk |
+---------------+
| first_tb      |
+---------------+
1 row in set (0.00 sec)

数据库first_db已经自动生成

由此，整个MySQL主从复制的过程就完成了，接下来，我们进行MySQL读写分离的安装与配置。


```
如果感觉查看不方便，可以下载配置文档：MySQL主从复制（Master-Slave）实践


##单独项目数据库MySQL my.cnf配置优化。

```
[client]
port   = 3306
socket = /opt/zbox/tmp/mysql/mysql.sock

[mysqld]
user       = nobody
port       = 3306
socket     = /opt/zbox/tmp/mysql/mysql.sock
datadir    = /opt/zbox/data/mysql
pid-file   = /opt/zbox/tmp/mysql/mysqld.pid
log_error  = /opt/zbox/logs/mysql_error.log
server-id  = 1

bind-address 			= 127.0.0.1
key_buffer              = 16M
max_allowed_packet      = 1M
table_open_cache        = 64
sort_buffer_size        = 512K
net_buffer_length       = 8K
read_buffer_size        = 256K
read_rnd_buffer_size    = 512K
myisam_sort_buffer_size = 8M
skip-external-locking
default-storage-engine=MyISAM

[mysqldump]
quick
max_allowed_packet = 16M

[mysql]
no-auto-rehash

[isamchk]
key_buffer       = 20M
sort_buffer_size = 20M
read_buffer      = 2M
write_buffer     = 2M

[myisamchk]
key_buffer       = 20M
sort_buffer_size = 20M
read_buffer      = 2M
write_buffer     = 2M

[mysqlhotcopy]
interactive-timeout

```

##mysql 远程连接不上，由于bind-address问题

```
MySQL远程连接ERROR 2003 (HY000):Can't connect to MySQL server on'XXXXX'(111) 的问题
Can t connect to MySQL server on 16报错。
刚安装了MySQL服务器，使用远程管理工具总是连接不上，因为知道mysql的默认端口是3306，于是使用telnet连接这个端口，
telnet 192.168.1.10 3306

还是连接不上，于是怀疑是防火墙问题，便将防火墙关掉，
service iptables stop
再次telnet，还是没办法连上这个端口，然后通过netstat查看3306的端口状态是怎么样的
netstat -apn|grep 3360
终于发现了一个比较奇怪的东西
tcp        0      0 127.0.0.1:3306            0.0.0.0:*               LISTEN      3783/mysqld
上面标红的地方，监听端口正常，但却绑定了本地回旋地址，难怪总是连接不上，于是查了下资料，找到了解决办法：
修改mysql的配置文件/etc/mysql/my.conf，将bind-address后面增加远程访问IP地址或者禁掉这句话就可以让远程机登陆访问了。

bind-address = 127.0.0.1

注释掉。

记得要重启mysql服务哦
service mysql restart

就可以登录了。


```

##mysql启动错误：mysql.sock丢失


```
服务是启动的。
netstat -apn|grep 3360
有启动进程。

[root@zentao proc]# mysql  -u root -p
Enter password:
ERROR 2002 (HY000): Can't connect to local MySQL server through socket '/var/lib/mysql/mysql.sock' (40)

这个错误，我后来看了下我的my.cnf 

路径指定的路径mysql.sock
socket     = /opt/zbox/tmp/mysql/mysql.sock

然后做了个软连接到/var/lib/mysql/mysql.sock ，因为安装客户端MySQL的默认是指定路径。所以做个软连接过去。

ln -s /opt/zbox/tmp/mysql/mysql.sock  /var/lib/mysql/mysql.sock

就可以登录了。

root@zentao mysql]# ln -s /opt/zbox/tmp/mysql/mysql.sock /var/lib/mysql/mysql.sock
[root@zentao mysql]# mysql -uroot -p
Enter password:
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 6
Server version: 5.5.45 Source distribution

Copyright (c) 2000, 2014, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
+--------------------+


设置权限时候把root权限本地登录localhost删除了。
登录出现报错。
ERROR 2002 (HY000): Can't connect to local MySQL server through socket '/var/lib/mysql/mysql.sock' (40)
发现/opt/zbox/tmp/mysql/mysql.sock
文件丢失了。
/var/lib/mysql/mysql .sock 链接无效，被删除了。
find / -name mysql.sock
显示为空，未查询到mysql.sock文件，mysql.sock文件丢失了。

重启后发现mysql.sock重启服务器未自动生成。

在次登录就成功了。


```
##Mysql配置为Master/Slave的常用维护命令

```
master端：
show master status;   #查看状态：
show processlist;     #查看slave下MySQL进程信息
reset master;         #慎用，将清空日志及同步position

slave端：
CHANGE MASTER TO MASTER_LOG_FILE=’master.000019′;
show slave status;
show slave logs;
show processlist;
reset slave;          #慎用，将清空slave配置信息、日志及同步position

在从服务器上跳过错误事件
mysql>stop slave;
mysql>set global sql_slave_skip_counter = n  (跳过主服务器中的接下来的 n 个事件。此命令对于由语句引起的复制终止有效。仅在从服务器线程没运行的时候有效);
mysql>start slave;


mysql 主服务器中同步用户 必须具  SUPER ，RELOAD，REPLICATION SLAVE 权限
当新加从服务器时，需要先在从库上 load data master; 保证和其他从库数据一致

set global sql_slave_skip_counter=n    # 客户端运行，用来跳过几个事件，只有当同步进程出现错误而停止的时候才可以执行。
reset master                           #主机端运行,清除所有的日志，这条命令就是原来的flush master
reset slave                            #从机运行，清除日志同步位置标志，并重新生成master.info
虽然重新生成了master.info,但是并不起用，最好，将从机的mysql进程重启一下，
load table tblname from master #从机运行，从主机端重读指定的表的数据，每次只能读取一个，受timeout时间限制，需要调整timeout时间。执行这个命令需要同步账号有reload和super权限。以及对相应的库有select权限。如果表比较大，要增加net_read_timeout 和 net_write_timeout的值
load data from master #从机执行，从主机端重新读入所有的数据。执行这个命令需要同步账号有reload和super权限。以及对相应的库有select权限。如果表比较大，要增加net_read_timeout 和 net_write_timeout的值
change master to master_def_list #在线改变一些主机设置，多个用逗号间隔,比如


change master to
master_host=’master2.mycompany.com’,
master_user=’replication’,
master_password=’bigs3cret’
master_pos_wait() #从机运行
show master status #主机运行，看日志导出信息
show slave hosts #主机运行，看连入的从机的情况。
show slave status (slave)
show master logs (master)
show binlog events [ in 'logname' ] [ from pos ] [ limit [offset,] rows ]
purge [master] logs to ‘logname’ ; purge [master] logs before ‘date’


//显示所有本机上的二进制日志
mysql> SHOW MASTER LOGS;


//删除所有本机上的二进制日志
mysql> RESET MASTER;


//删除所有创建时间在binary-log.xxx之前的二进制日志
mysql> PURGE MASTER LOGS TO ‘binary-log.xxx’;


//只保留最近6天的日志，之前的都删掉
find /var/intra -type f -mtime +6 -name “*.log” -exec rm -f {} \;


//用键盘左上角（也就是Esc下面）那个键包围起来，说明是命令。-1d是昨天，以此类推-1m是上个月等等
day=`/bin/date -v -1d +%Y%m%d`;


//给文件改名
mv xxx.log xxx-${day}.log;


//这里还要加上数据库的用户名密码，作用是更新日志（包括二进制日志和查询日志等等）
mysqladmin flush-logs
```
