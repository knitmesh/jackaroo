##### redis主从复制特点

1. 同一个Master可同步多个Slaves。
2. Slave可接受其它Slaves的连接和同步请求，有效分载Master的同步压力。因此可将Redis的Replication架构视为图结构。
3. Master Server以非阻塞的方式为Slaves提供服务。在Master-Slave同步期间，客户端仍可提交查询或修改请求。
4. Slave Server以非阻塞的方式完成数据同步，在首次同步数据时会阻塞不能处理客户端请求。之后在同步期间，客户端提交查询请求，Slave Server返回同步之前的数据。
5.主从复制可以用来提高系统的可伸缩性， 可将多个Slave服务器专门提供只读请求，如可用来做sort操作或做简单的数据冗余。
6. 可禁用Master数据持久化操作，数据持久化操作交给Slaves完成，避免在Master中要有独立的进程来完成此操作。只需要注释掉master 配置文件中的所有save配置，然后只在slave上配置数据持久化。

##### redis主从复制原理：

在Slave启动并连接到Master后，将主动发送SYNC命令。此后Master将启动后台存盘进程，将数据库快照保存到文件中，同时收集新的写命令（增、删、改）并缓存起来，在后台进程执行写文件。完毕后，Master将传送整个数据库文件到Slave，以完成一次完全同步。而Slave服务器在接收到数据库文件数据之后将其存盘并加载到内存恢复数据库快照到slave上。此后，Master继续将所有已经收集到的修改命令，和新的修改命令依次传送给Slaves，Slave将在本次执行这些数据修改命令，从而达到最终的数据同步。从master到slave的同步数据的命令和从 client发送的命令使用相同的协议格式。

如果Master和Slave之间的链接出现断连现象，Slave可以自动重连Master，但是在连接成功之后，一次完全同步将被自动执行。

如果master同时收到多个 slave发来的同步连接命令，只会使用启动一个进程来写数据库镜像，然后发送给所有slave。

##### redis主从复制配置

1.复制一份配置文件为从机所用
```
[root@localhost ~]# cp -p /etc/redis.conf /etc/redis-slave.conf 
[root@localhost ~]# vi /etc/redis-slave.conf 
```

##### 主从机配置不同之处

pid 文件
    主机：pidfile /var/run/redis/redis.pid
    从机：pidfile /var/run/redis/redis-slave.pid

端口
    主机：port 6379
    从机：port 6380

日志文件  
    主机：logfile /var/log/redis/redis.log
    从机：logfile /var/log/redis/redis-slave.log  

数据文件
    主机：dbfilename dump.rdb
    从机：dbfilename dump-slave.rdb

设置该数据库为其他数据库的从数据库，设置主服务的IP及端口
    从机：slaveof 127.0.0.1 6379    

##### 其它后继数据备份工作

1. 用redis-cli bgsave 命令每天凌晨一次持久化一次master redis上的数据，并CP到其它备份服务器上。
2. 用redis-cli bgrewriteaof 命令每半小时持久化一次 slave redis上的数据，并CP到其它备份服务器上。
3. 写个脚本 ，定期get master和slave上的key,看两个是否同步，如果没有同步，及时报警。