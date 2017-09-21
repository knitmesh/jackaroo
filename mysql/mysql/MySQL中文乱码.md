# mysql中文乱码

mysql中文乱码解决方法：将mysql数据库编码统一utf8

查看数据库编码：

><span style="font-size: 16px;"><strong>show variables like 'character%';</strong></span>
 
编辑/etc/my.cnf
```
[mysql]
default-character-set=utf8
[mysqld]
datadir=/var/lib/mysql
socket=/var/lib/mysql/mysql.sock
user=mysql
# Disabling symbolic-links is recommended to prevent assorted security risks
symbolic-links=0
default-character-set = utf8
character_set_server = utf8
#lower_case_table_names=1  忽略大小写
[mysqld_safe]
log-error=/var/log/mysqld.log
pid-file=/var/run/mysqld/mysqld.pid
default-character-set = utf8
[mysql.server]
default-character-set = utf8
[client]
default-character-set = utf8
　　
```


修改完成后，`service mysql restart`重启mysql服务就生效。

注意：[mysqld]字段与[mysql]字段是有区别的。　　

如果上面的都修改了还乱码，那剩下问题就一定在connection连接层上。

解决方法是在发送查询前执行一下下面这句（直接写在SQL文件的最前面）：

>SET NAMES 'utf8';

它相当于下面的三句指令：

```
SET character_set_client = utf8;
SET character_set_results = utf8;
SET character_set_connection = utf8;
```

执行`show variables like 'character%' `跟下面一样说明修改成功

```
mysql> show variables like 'character%';
+--------------------------+----------------------------+
| Variable_name            | Value                      |
+--------------------------+----------------------------+
| character_set_client     | utf8                       |
| character_set_connection | utf8                       |
| character_set_database   | utf8                       |
| character_set_filesystem | binary                     |
| character_set_results    | utf8                       |
| character_set_server     | utf8                       |
| character_set_system     | utf8                       |
| character_sets_dir       | /usr/share/mysql/charsets/ |
+--------------------------+----------------------------+
8 rows in set (0.00 sec)
```

