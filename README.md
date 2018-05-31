# That'll make this place look like a gopher hole.

## 初衷(Original Intention)
登山的乐趣不在于到达山顶，而在于到达山顶的过程，在此记录这个过程！

这里所记录的，只是我对自己学习过程的一个记录，而且只是一个初步的学习，其中难免会有理解不到位，或者是有错误的地方，如果有高人飘过，还请不吝赐教。

志之所趋。无远勿届。穷山距海。不能限也。

## 目录(Contents)

### 记录一些使用过的脚本；
* [常用脚本](script)
    * [shell脚本](script/shell)
        * [openstack script](script/shell/openstack)
        * [install script](script/shell/install)
        * [多主机批量设置网卡](script/shell/batch_set_network.md)
        * [清理ci构建的旧版本包](script/shell/clear_packages.md)
        * [批量配置免密登录](script/shell/openstack%20批量配置免密登录.md)
        * [批量ping主机](script/shell/ping_hosts.md)
        * [单主机批量设置网卡](script/shell/set_network.md)
        * [批量修改主机名](script/shell/批量修改主机名.md)
    * [python脚本](script/python)
        * [prepare docker file](script/python/prepare.py)
        
### Nginx相关
* [Nginx相关](Nginx)
    * [Nginx基本配置备忘](Nginx/Nginx基本配置备忘)       
    * [将Nginx配置为Web服务器](Nginx/如何将%20Nginx%20配置为Web服务器)

### mq相关
* [学习笔记](learnmq)
    * [rabbitmq基本概念](learnmq/doc/rabbitmq基本概念.md)
    * [rabbitmq常用管理命令](learnmq/doc/rabbitmq常用管理命令.md)
    * [centos7下安装rebbitmq](learnmq/doc/centos7下安装rebbitmq.md)
    * [demo](learnmq/python)
    
### 关于全文检索的相关技术贴
* [全文检索](lucene)
    * [ElasticSearch存储相关](lucene/ElasticSearch存储相关.md)

### sql相关；
* [mysql](mysql)
    * [README.md](mysql/README.md)
    * [数据库设计总结](mysql/MySQL%20数据库设计总结) 
    * [数据导入导出](mysql/导入导出数据)
    * [MySQL读写分离](mysql/MySQL读写分离)   
    * [MySQL大表优化方案](mysql/MySQL大表优化方案)   
    * [mysql分库分表](mysql/mysql分库分表)
    * [配置](mysql/configs)
        * [单独项目数据库MySQL my.cnf配置优化。](mysql/configs/单独项目数据库MySQL%20my.cnf配置优化。)
        * [my.cnf优化配置](mysql/configs/my.cnf%20优化配置)
    * [主从](mysql/Master-slave)
        * [mysql主从脚本思路](mysql/Master-slave/mysql主从脚本思路)
        * [mysql主从环境搭建](mysql/Master-slave/mysql主从环境搭建)
        * [Mysql配置为Master:Slave的常用维护命令](mysql/Master-slave/Mysql配置为Master:Slave的常用维护命令)
    * [分区](mysql/分区)
        * [](mysql/分区/MySQL表的四种分区类型)   
    * [操作系统配置](mysql/操作系统配置优化)   
        * [操作系统配置优化](mysql/操作系统配置优化/操作系统配置优化) 
    * [日志](mysql/日志)
        * [mysql日志详细解析](mysql/日志/mysql日志详细解析)  
    * [问题记录](mysql/问题解决) 
    * [练习题](mysql/sql_exercises)
    * [其他](mysql/mysql)   
        * [1970年以前的时间戳处理](mysql/mysql/1970年以前的时间戳处理.md)   
        * [BTREE](mysql/mysql/BTREE.md)   
        * [BTREE索引哈希索引](mysql/mysql/BTREE索引%20哈希索引.md)   
        * [HA配置优化](mysql/mysql/HA配置优化.md)   
        * [InnoDB](mysql/mysql/InnoDB.md)   
        * [MySQL中文乱码](mysql/mysql/MySQL中文乱码.md)   
        * [MySql性能优化](mysql/mysql/MySql性能优化.md)   
        * [Mysql性能相关参数，与检测办法](mysql/mysql/Mysql性能相关参数，与检测办法.md)   
        * [processlist](mysql/mysql/processlist.md)   
        * [Sql分析函数EXPLAIN](mysql/mysql/Sql分析函数%20EXPLAIN.md)   
        * [分库分表分布式%20全局唯一ID解决方案](mysql/mysql/分库分表分布式%20全局唯一ID解决方案（系列1）.md)   

    * [文档收藏](mysql/mysql.md)   
    
    
### nosql相关
* [memcache](memcache)
    * [深入理解memcache](memcache/深入理解memcache.md)   
    * [memcache总结](memcache/memcache总结.md)   
    * [云平台缓存场景概述](memcache/云平台缓存场景概述.md)  
     
### linux相关
* [命令](command/)
    * [Sed与Awk学习笔记](command/Sed与Awk学习笔记)   
    
    * [服务配置](service-configuration)
        * [haproxy](service-configuration/haproxy)
        * [keepalived](service-configuration/keepalived)
        * [mongodb](service-configuration/mongodb)
        * [rabbitmq](service-configuration/rabbitmq)
        * [keepalived](service-configuration/keepalived)
        * [mysql](service-configuration/my.cnf)
        * [redis主从复制配置](service-configuration/redis主从复制配置.md)
        * [redis配置说明](service-configuration/redis配置说明.md)

### 版本管理
* [git](learngit)
    * [git命令](learngit/command.md)

### 编程语言相关    
* [python](learnpy)
    * [库](learnpy/lib-demo/README.md)   
        * [APScheduler](learnpy/lib-demo/APScheduler.md)   
        * [eventlet](learnpy/lib-demo/eventlet.md)   
        * [futurist](learnpy/lib-demo/futurist.md)   
        * [greenlet](learnpy/lib-demo/greenlet.md)    
    * [面试题](learnpy/interviewpy/Readme.md)
    * [进程线程协程](learnpy/interviewpy/docs/进程线程协程.md)
    * [python垃圾回收](learnpy/interviewpy/docs/python的垃圾回收.md)

* [js](learnjs)
    * [面试题](learnjs/SUMMARY.md)
    
### 特别感谢(Special Thanks)
整理的部分资料中有些引用了各博主大佬的内容, 能署名已尽量署名, 若有疏忽还请见谅, 在此感谢。