# mysql的HA配置优化


## 1.记录慢速查询

在一个 SQL 服务器中，数据表都是保存在磁盘上的。
索引为服务器提供了一种在表中查找特定数据行的方法，而不用搜索整个表。

当必须要搜索整个表时，就称为表扫描。
通常来说，您可能只希望获得表中数据的一个子集，因此全表扫描会浪费大量的磁盘 I/O，因此也就会浪费大量时间。
当必须对数据进行连接时，这个问题就更加复杂了，因为必须要对连接两端的多行数据进行比较。

当然，表扫描并不总是会带来问题；
有时读取整个表反而会比从中挑选出一部分数据更加有效（服务器进程中查询规划器用来作出这些决定）。
如果索引的使用效率很低，或者根本就不能使用索引，则会减慢查询速度，而且随着服务器上的负载和表大小的增加，这个问题会变得更加显著。

执行时间超过给定时间范围的查询就称 为`慢速查询`。

配置 mysqld 将这些慢速查询记录到适当命名的慢速查询日志中。管理员然后会查看这个日志来帮助他们确定应用程序中有哪些部分需要进一步调查。
my.cnf中配置

```
[mysqld]
slow-query-log = on # 开启慢查询功能
slow_query_log_file = /usr/local/mysql/data/slow-query.log # 慢查询日志存放路径与名称
long_query_time= 5 # 查询时间超过5s的查询语句
### log-queries-not-using-indexes = on # 列出没有使用索引的查询语句
```

## 2.对查询进行缓存

很多 LAMP/LNMP 应用程序都严重依赖于数据库，但却会反复执行相同的查询。
每次执行查询时，数据库都必须要执行相同的工作——对查询进行分析，确定如何执行查询，从磁盘中加载信息，然后将结果返回给客户机。

MySQL 有一个特性称为查询缓存，它将（后面会用到的）查询结果保存在内存中。在很多情况下，这会极大地提高性能。
不过，问题是查询缓存在默认情况下是`禁用`的。

>将 query_cache_size = 32M 添加到 /etc/my.conf 中可以启用 32MB 的查询缓存。

```
mysql> SHOW VARIABLES LIKE 'have_query_cache'; 
     Variable_name    | Value | 
+------------------+-------+ 
| have_query_cache | YES   | 
```

如发现结果中query_cache_size =0，则没设置，设置的方法为在my.ini中，设置 
>query_cache_size=128M 

增加一行：query_cache_type=1 
监视查询缓存
```
mysql> SHOW STATUS LIKE 'qcache%';
+-------------------------+------------+
| Variable_name           | Value      |
+-------------------------+------------+
| Qcache_free_blocks      | 5216       |
| Qcache_free_memory      | 14640664   |
| Qcache_hits             | 2581646882 |
| Qcache_inserts          | 360210964  |
| Qcache_lowmem_prunes    | 281680433  |
| Qcache_not_cached       | 79740667   |
| Qcache_queries_in_cache | 16927      |
| Qcache_total_blocks     | 47042      |
+-------------------------+------------+
Qcache_free_blocks                    缓存中相邻内存块的个数。数目大说明可能有碎片。 FLUSH QUERY CACHE       会对缓存中的碎片进行整理，从而得到一个空闲
Qcache_free_memory                    缓存中的空闲内存
Qcache_hits                      每次查询在缓存中命 中时就增大
Qcache_inserts                    每次插入一个查询时就增大。
Qcache_lowmem_prunes                    缓存出现内存不足并且必须要 进行清理以便为更多查询提供空间的次数。
Qcache_not_cached                    不适合进行缓 存的查询的数量，通常是由于这些查询不是 SELECT 语句。                
Qcache_queries_in_cache                    当前缓存的查询（和响应）的数量
Qcache_total_blocks                    缓 存中块的数量。
```

## 3.强制限制

您可以在 mysqld 中强制一些限制来确保系统负载不会导致资源耗尽的情况出现，下面给出了 my.cnf 中与资源有关的一些重要设置。

```
max_connections=500 （〈＝16384，默认保留一个管理员连接）
wait_timeout=10
max_connect_errors = 100
```

连接最大个数是在第一行中进行管理的。与 Apache 中的 MaxClients 类似，其想法是确保只建立服务允许数目的连接。
要确定服务器上目前建立过的最大连接数，防止： Too many connections错误  

>请执行 SHOW STATUS LIKE ‘max_used_connections’ 查看

wait_timeout=10告诉 mysqld 终止所有空闲时间超过 10 秒的连接。
在 LNMP 应用程序中，连接数据库的时间通常就是 Web 服务器处理请求所花费的时间。
有时候，如果负载过重，连接会挂起，并且会占用连接表空间。
如果有多个交互用户或使用了到数据库的持久连接，那么将这个值设 低一点并不可取！
>Error:MySQL has gone away

最后一行是一个安全的方法。
如果一个主机在连接到服务器时有问题，并重试很多次后放弃，那么这个主机就会被锁定，直到 FLUSH HOSTS 之后才能运行。
默认情况下，10 次失败就足以导致锁定了。
将这个值修改为 100 会给服务器足够的时间来从问题中恢复。
如果重试 100 次都无法建立连接，那么使用再高的值也不会有太多帮助，可能它根本就无法连接。

显示打开表的活动
```
mysql> SHOW STATUS LIKE 'open%tables‘;
    | Open_tables   | 6000|
     | Opened_tables | 200    |
```

说明目前有6000个表是打开的，有1个表需要打开，因为现在缓存中已经没有可用文件描述符了（由于统计信息在前面已经清除了，因此可能会存在 6,000 个打开表中只有 200个打开记录的情况）。
如果Opened_tables随着重新运行 SHOW STATUS 命令快速增加，就说明缓存命中率不够。
如果 Open_tables 比 table_cache 设置小很多，就说明该值太大了（不过有空间可以增长总不是什么坏事）。
例如，使用 table_cache = 6000 可以调整表的缓存。

确定表扫描比率
```
mysql> SHOW STATUS LIKE 'com_select';
| Com_select    | 1｜
mysql> SHOW STATUS LIKE ‘handler_read_rnd_next’;
| Handler_read_rnd_next | 2|
```

MySQL 也会分配一些内存来读取表。理想情况下，索引提供了足够多的信息，可以只读入所需要的行，但是有时候查询（设计不佳或数据本性使然）需要读取表中大量数 据。
要理解这种行为，需要知道运行了多少个 SELECT 语句，以及需要读取表中的下一行数据的次数（而不是通过索引直接访问）
`Handler_read_rnd_next / Com_select `得出了表扫描比率 ，如果该值超过 4000，就应该查看 read_buffer_size，
例如 read_buffer_size = 4M。如果这个数字超过了 8M，就应该对这些查询进行调优了！

## 4.监控工具
>Top

>Htop

>Cacti 图形形式查看结果
