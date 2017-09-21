Mysql服务启动之后，我们可以使用`show variables`和`show status` 命令可以查看mysql服务的静态参数值和动态运行状态信息。
## show variables
show variables是查看数据库启动后不会动弹更改的值，比如缓冲区大小、字符集、数据文件名等。
## show status
show status是查看数据库运行期间的动态变化信息，比如锁等待、当前连接数等。

<br>

# 影响MySQL性能的重要参数

    主要介绍的是使用MyISAM存储引擎的key_buffer_size和table_cache
    以及使用使用InnoDB存储引擎的一些以innodb_开头的参数
    
## 1.key_buffer_size

该参数是用来设置索引块（Index Blocks）缓存的大小，它被索引线程共享，此参数只使用MyISAM存储引擎。MySQL5.1之后的版本，可以将指定的表索引缓存入指定的key_buffer,这样可以降低线程之间的竞争。
```
索引缓存概述
<br>
MyISAM存储引擎和其他很多数据库系统一样，采用了一种将最经常访问的表保存在内存中的策略。对应索引区块来说，它维护者一个叫做索引缓存（索引缓冲）的结构体，这个结构体中存放着许多哪些经常使用的索引区块的缓冲区块。对应数据区块来说，Mysql主要依靠系统的本地文件系统缓存。有了索引缓冲后，线程之间不再是串行地访问索引缓存。多个线程可以并行地访问索引缓存。可以设置多个索引缓存，同时也能指定数据表索引到特定的缓存中。
```
### 创建一个索引缓存
```
set global 缓存索引名.key_buffer_size=100*1024;
```
global是全局限制，表示对每一个新的会话（连接）都有效。

### 修改一个索引缓存
和创建一个索引缓存一样，都是
```
set global 缓存索引名.key_buffer_size=100*1024;
```

将相关表的索引放到自己创建的索引缓存中
格式：
```
cache index 表名1,表名2 in 索引缓存
```
将t1、t2、t3表中的索引放到my_cache索引缓存中
因为t1表式InnoDB表，t2，t3表为MyISAM表，故只有t2、t3表中的索引可以放到my_cache缓存中。

### 将索引放到默认的kef_buffer中

可以使用load index into cache +表名

### 删除索引缓存
将其索引缓冲大小设置为了0，就可以删除了，注意不能删除默认的key_buffer。
### 配置mysql服务器启动时自动加载索引缓存
在MySQL配置文件中添加如下内容（在Windows下叫my.ini，在Linux下叫my.cnf）
my_cache.key_buffer_size=1G  #指定索引缓存区大小
init_file=/usr/local/mysql/init_index.sql#在该文件中指定要加载到缓存区德索引


## 2.table_cache
概述
这个参数表示数据库用户打开表的缓冲数量，table_cache与max_connections有关。当某一连接访问一个表时，MySQL会检查当前已缓存表的数量，如果该表已经在缓冲中打开，则直接访问缓存中的表，如果为被缓存，则会将当前表添加进缓存并进行查询。
在执行缓存操作之前，table_cache用于限制缓存表的最大数目，如果当前已经缓存的表未达到table_cache，则会将新表添加进来；若已经达到此值，MySQL将根据缓存表的最后查询时间、查询率等规则释放之前缓存的表，添加新表。
参数调优
通过检查mysqld的状态变量open_tables和opend_tables确定table_cache这个参数的大小。open_tables代表当前打开的表缓冲数量，如果执行flush tables，则系统会关闭一些当前没有使用的表缓存，使得open_tables值减少。opend_tables表示曾经打开的表缓存数，会一直进行累加，不会因为执行flush tables操作，有所减少。

## 3.Innodb_buffer_pool_size
这个参数定义了InnoDB存储引擎的表数据和索引数据的最大内存缓存区大小。和MyISAM存储引擎不同，MyISAM的key_buffer_size只缓存索引键，而Innodb_buffer_pool_size同时为数据块和索引块做了缓存，这个只设的越高，访问表中的数据需要的磁盘I/O就越少。但是设置的过大，会导致物理内存竞争过大。
## 4.Innodb_flush_log_at_trx_commit
这个参数是控制缓存区中的数据写入到日志文件以及日志文件数据刷新到磁盘的操作时机。默认值为1。可以有以下值：
0：日志缓冲每秒一次地写到日志文件，并对日志文件作向磁盘刷新操作，但事务提交不做任何操作。
1：每个事务提交时，日志缓冲被写到日志文件，并且对日志文件做向磁盘刷新操作。
2：每个事务提交时候，日志缓冲被写到日志文件，但是不对日志文件作向磁盘刷新操作，对日志文件每秒向磁盘做一次刷新操作。
## 5.Innodb_additional_mem_pool_size
这个参数用来存在数据库结构和其他内部数据结果的内存池的大小。
## 6.Innodb_log_buffer_size
日志缓存大小
## 7.innodb_log_file_size
日志组中每个日志文件的大小
## 8.innodb_lock_wait-timeout
Mysql可以自动地监控行锁导致的死锁并经行相应的处理，但是对于表锁导致的死锁，不能自动检测，该参数主要是用于在出现行死锁时候等待指定的时间后回滚。
## 9.Innodb_support_xa
设置是否支持分布式事务，默认为ON或者1，表示支持。

