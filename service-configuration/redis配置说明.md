Redis的配置文件格式类似于java的properties文件，是一个一个的键值对，但是redis的键值对之间是用空格分隔的。同样以#号开头的行为注释行

```
# By default Redis does not run as a daemon. Use 'yes' if you need it.
# Note that Redis will write a pid file in /var/run/redis.pid when daemonized.
#Redis默认不是以守护进程的方式运行，可以通过该配置项修改，使用yes启用守护进程
daemonize no




# When running daemonized, Redis writes a pid file in /var/run/redis.pid by
# default. You can specify a custom pid file location here.
#当 Redis 以守护进程的方式运行的时候，Redis 默认会把 pid 文件放在/var/run/redis.pid
#可配置到其他地址，当运行多个 redis 服务时，需要指定不同的 pid 文件和端口
pidfile /var/run/redis.pid




# Accept connections on the specified port, default is 6379.
# If port 0 is specified Redis will not listen on a TCP socket.
#端口
port 6379




# If you want you can bind a single interface, if the bind option is not
# specified all the interfaces will listen for incoming connections.
#指定Redis可接收请求的IP地址,不设置将处理所有请求,建议生产环境中设置
# bind 127.0.0.1




# Close the connection after a client is idle for N seconds (0 to disable)
#客户端连接的超时时间,单位为秒,超时后会关闭连接
timeout 0




# Set server verbosity to 'debug'
# it can be one of:
# debug (a lot of information, useful for development/testing)
# verbose (many rarely useful info, but not a mess like the debug level)
# notice (moderately verbose, what you want in production probably)
# warning (only very important / critical messages are logged)
#日志记录等级，4个可选值
loglevel notice




# Specify the log file name. Also 'stdout' can be used to force
# Redis to log on the standard output. Note that if you use standard
# output for logging but daemonize, logs will be sent to /dev/null
#配置 log 文件地址,默认打印在命令行终端的窗口上，也可设为/dev/null屏蔽日志、
logfile stdout




# Set the number of databases. The default database is DB 0, you can select
# a different one on a per-connection basis using SELECT where
# dbid is a number between 0 and 'databases'-1
#设置数据库的个数,可以使用 SELECT 命令来切换数据库。
databases 16




#
# Save the DB on disk:
#
#   save
#
#   Will save the DB if both the given number of seconds and the given
#   number of write operations against the DB occurred.
#
#   In the example below the behaviour will be to save:
#   after 900 sec (15 min) if at least 1 key changed
#   after 300 sec (5 min) if at least 10 keys changed
#   after 60 sec if at least 10000 keys changed
#
#   Note: you can disable saving at all commenting all the "save" lines.
#设置 Redis 进行数据库镜像的频率。保存数据到disk的策略
#900秒之内有1个keys发生变化时
#30秒之内有10个keys发生变化时
#60秒之内有10000个keys发生变化时
save 900 1
save 300 10
save 60 10000




# Compress string objects using LZF when dump .rdb databases?
# For default that's set to 'yes' as it's almost always a win.
# If you want to save some CPU in the saving child set it to 'no' but
# the dataset will likely be bigger if you have compressible values or keys.
#在进行镜像备份时,是否进行压缩
rdbcompression yes




# The filename where to dump the DB
#镜像备份文件的文件名
dbfilename dump.rdb




# The working directory.
#
# The DB will be written inside this directory, with the filename specified
# above using the 'dbfilename' configuration directive.
# 
# Also the Append Only File will be created inside this directory.
# 
# Note that you must specify a directory here, not a file name.
#数据库镜像备份的文件放置的路径
#路径跟文件名分开配置是因为 Redis 备份时，先会将当前数据库的状态写入到一个临时文件
#等备份完成时，再把该临时文件替换为上面所指定的文件
#而临时文件和上面所配置的备份文件都会放在这个指定的路径当中
#默认值为 ./
dir /var/lib/redis/




# Master-Slave replication. Use slaveof to make a Redis instance a copy of
# another Redis server. Note that the configuration is local to the slave
# so for example it is possible to configure the slave to save the DB with a
# different interval, or to listen to another port, and so on.
#设置该数据库为其他数据库的从数据库
#slaveof   当本机为从服务时，设置主服务的IP及端口
# slaveof




# If the master is password protected (using the "requirepass" configuration
# directive below) it is possible to tell the slave to authenticate before
# starting the replication synchronization process, otherwise the master will
# refuse the slave request.
#指定与主数据库连接时需要的密码验证
#masterauth  当本机为从服务时，设置主服务的连接密码
# masterauth




# When a slave lost the connection with the master, or when the replication
# is still in progress, the slave can act in two different ways:
#
# 1) if slave-serve-stale-data is set to 'yes' (the default) the slave will
#    still reply to client requests, possibly with out of data data, or the
#    data set may just be empty if this is the first synchronization.
#
# 2) if slave-serve-stale data is set to 'no' the slave will reply with
#    an error "SYNC with master in progress" to all the kind of commands
#    but to INFO and SLAVEOF.
#当slave丢失与master的连接时，或slave仍然在于master进行数据同步时（未与master保持一致）
#slave可有两种方式来响应客户端请求：
#1)如果 slave-serve-stale-data 设置成 'yes'(默认)，slave仍会响应客户端请求,此时可能会有问题
#2)如果 slave-serve-stale-data 设置成 'no'，slave会返回"SYNC with master in progress"错误信息，但 INFO 和SLAVEOF命令除外。
slave-serve-stale-data yes




# Require clients to issue AUTH before processing any other
# commands.  This might be useful in environments in which you do not trust
# others with access to the host running redis-server.
#
# This should stay commented out for backward compatibility and because most
# people do not need auth (e.g. they run their own servers).
# 
# Warning: since Redis is pretty fast an outside user can try up to
# 150k passwords per second against a good box. This means that you should
# use a very strong password otherwise it will be very easy to break.
#设置客户端连接后进行任何其他指定前需要使用的密码
#redis速度相当快，一个外部用户在一秒钟进行150K次密码尝试，需指定强大的密码来防止暴力破解
# requirepass foobared




# Set the max number of connected clients at the same time. By default there
# is no limit, and it's up to the number of file descriptors the Redis process
# is able to open. The special value '0' means no limits.
# Once the limit is reached Redis will close all the new connections sending
# an error 'max number of clients reached'.
#限制同时连接的客户数量。
#当连接数超过这个值时，redis 将不再接收其他连接请求，客户端尝试连接时将收到 error 信息
# maxclients 128




# Don't use more memory than the specified amount of bytes.
# When the memory limit is reached Redis will try to remove keys
# accordingly to the eviction policy selected (see maxmemmory-policy).
#
# If Redis can't remove keys according to the policy, or if the policy is
# set to 'noeviction', Redis will start to reply with errors to commands
# that would use more memory, like SET, LPUSH, and so on, and will continue
# to reply to read-only commands like GET.
#
# This option is usually useful when using Redis as an LRU cache, or to set
# an hard memory limit for an instance (using the 'noeviction' policy).
#
# WARNING: If you have slaves attached to an instance with maxmemory on,
# the size of the output buffers needed to feed the slaves are subtracted
# from the used memory count, so that network problems / resyncs will
# not trigger a loop where keys are evicted, and in turn the output
# buffer of slaves is full with DELs of keys evicted triggering the deletion
# of more keys, and so forth until the database is completely emptied.
#
# In short... if you have slaves attached it is suggested that you set a lower
# limit for maxmemory so that there is some free RAM on the system for slave
# output buffers (but this is not needed if the policy is 'noeviction').
#设置redis能够使用的最大内存。
#达到最大内存设置后，Redis会先尝试清除已到期或即将到期的Key（设置过expire信息的key）
#在删除时,按照过期时间进行删除，最早将要被过期的key将最先被删除
#如果已到期或即将到期的key删光，仍进行set操作，那么将返回错误
#此时redis将不再接收写请求,只接收get请求。
#maxmemory的设置比较适合于把redis当作于类似memcached 的缓存来使用
# maxmemory 




# By default Redis asynchronously dumps the dataset on disk. If you can live
# with the idea that the latest records will be lost if something like a crash
# happens this is the preferred way to run Redis. If instead you care a lot
# about your data and don't want to that a single record can get lost you should
# enable the append only mode: when this mode is enabled Redis will append
# every write operation received in the file appendonly.aof. This file will
# be read on startup in order to rebuild the full dataset in memory.
#
# Note that you can have both the async dumps and the append only file if you
# like (you have to comment the "save" statements above to disable the dumps).
# Still if append only mode is enabled Redis will load the data from the
# log file at startup ignoring the dump.rdb file.
#
# IMPORTANT: Check the BGREWRITEAOF to check how to rewrite the append
# log file in background when it gets too big.
#redis 默认每次更新操作后会在后台异步的把数据库镜像备份到磁盘，但该备份非常耗时，且备份不宜太频繁
#redis 同步数据文件是按上面save条件来同步的
#如果发生诸如拉闸限电、拔插头等状况,那么将造成比较大范围的数据丢失
#所以redis提供了另外一种更加高效的数据库备份及灾难恢复方式
#开启append only 模式后,redis 将每一次写操作请求都追加到appendonly.aof 文件中
#redis重新启动时,会从该文件恢复出之前的状态。
#但可能会造成 appendonly.aof 文件过大，所以redis支持BGREWRITEAOF 指令，对appendonly.aof重新整理
appendonly no




# The name of the append only file (default: "appendonly.aof")
##更新日志文件名，默认值为appendonly.aof
# appendfilename appendonly.aof




# The fsync() call tells the Operating System to actually write data on disk
# instead to wait for more data in the output buffer. Some OS will really flush 
# data on disk, some other OS will just try to do it ASAP.
#
# Redis supports three different modes:
#
# no: don't fsync, just let the OS flush the data when it wants. Faster.
# always: fsync after every write to the append only log . Slow, Safest.
# everysec: fsync only if one second passed since the last fsync. Compromise.
#
# The default is "everysec" that's usually the right compromise between
# speed and data safety. It's up to you to understand if you can relax this to
# "no" that will will let the operating system flush the output buffer when
# it wants, for better performances (but if you can live with the idea of
# some data loss consider the default persistence mode that's snapshotting),
# or on the contrary, use "always" that's very slow but a bit safer than
# everysec.
#
# If unsure, use "everysec".
#设置对 appendonly.aof 文件进行同步的频率
#always 表示每次有写操作都进行同步,everysec 表示对写操作进行累积,每秒同步一次。
#no表示等操作系统进行数据缓存同步到磁盘，都进行同步,everysec 表示对写操作进行累积,每秒同步一次
# appendfsync always
appendfsync everysec
# appendfsync no




# Virtual Memory allows Redis to work with datasets bigger than the actual
# amount of RAM needed to hold the whole dataset in memory.
# In order to do so very used keys are taken in memory while the other keys
# are swapped into a swap file, similarly to what operating systems do
# with memory pages.
#
# To enable VM just set 'vm-enabled' to yes, and set the following three
# VM parameters accordingly to your needs.
#是否开启虚拟内存支持。
#redis 是一个内存数据库，当内存满时,无法接收新的写请求,所以在redis2.0后,提供了虚拟内存的支持
#但需要注意的，redis 所有的key都会放在内存中，在内存不够时,只把value 值放入交换区
#虽使用虚拟内存，但性能基本不受影响，需要注意的是要把vm-max-memory设置到足够来放下所有的key
vm-enabled no
# vm-enabled yes




# This is the path of the Redis swap file. As you can guess, swap files
# can't be shared by different Redis instances, so make sure to use a swap
# file for every redis process you are running. Redis will complain if the
# swap file is already in use.
#
# The best kind of storage for the Redis swap file (that's accessed at random) 
# is a Solid State Disk (SSD).
#
# *** WARNING *** if you are using a shared hosting the default of putting
# the swap file under /tmp is not secure. Create a dir with access granted
# only to Redis user and configure Redis to create the swap file there.
#设置虚拟内存的交换文件路径，不可多个Redis实例共享
vm-swap-file /tmp/redis.swap




# vm-max-memory configures the VM to use at max the specified amount of
# RAM. Everything that deos not fit will be swapped on disk *if* possible, that
# is, if there is still enough contiguous space in the swap file.
#
# With vm-max-memory 0 the system will swap everything it can. Not a good
# default, just specify the max amount of RAM you can in bytes, but it's
# better to leave some margin. For instance specify an amount of RAM
# that's more or less between 60 and 80% of your free RAM.
#设置开启虚拟内存后,redis将使用的最大物理内存大小。
#默认为0，redis将把他所有能放到交换文件的都放到交换文件中，以尽量少的使用物理内存
#即当vm-max-memory设置为0的时候,其实是所有value都存在于磁盘
#在生产环境下,需要根据实际情况设置该值,最好不要使用默认的 0
vm-max-memory 0




# Redis swap files is split into pages. An object can be saved using multiple
# contiguous pages, but pages can't be shared between different objects.
# So if your page is too big, small objects swapped out on disk will waste
# a lot of space. If you page is too small, there is less space in the swap
# file (assuming you configured the same number of total swap file pages).
#
# If you use a lot of small objects, use a page size of 64 or 32 bytes.
# If you use a lot of big objects, use a bigger page size.
# If unsure, use the default :)
#设置虚拟内存的页大小
如果 value 值比较大,如要在 value 中放置博客、新闻之类的所有文章内容，就设大一点
vm-page-size 32




# Number of total memory pages in the swap file.
# Given that the page table (a bitmap of free/used pages) is taken in memory,
# every 8 pages on disk will consume 1 byte of RAM.
#
# The total swap size is vm-page-size * vm-pages
#
# With the default of 32-bytes memory pages and 134217728 pages Redis will
# use a 4 GB swap file, that will use 16 MB of RAM for the page table.
#
# It's better to use the smallest acceptable value for your application,
# but the default is large in order to work in most conditions.
#设置交换文件的总的 page 数量
#注意page table信息是放在物理内存中，每8个page 就会占据RAM中的 1 个 byte
#总的虚拟内存大小 = vm-page-size * vm-pages
vm-pages 134217728




# Max number of VM I/O threads running at the same time.
# This threads are used to read/write data from/to swap file, since they
# also encode and decode objects from disk to memory or the reverse, a bigger
# number of threads can help with big objects even if they can't help with
# I/O itself as the physical device may not be able to couple with many
# reads/writes operations at the same time.
#
# The special value of 0 turn off threaded I/O and enables the blocking
# Virtual Memory implementation.
#设置 VM IO 同时使用的线程数量。
vm-max-threads 4




# Hashes are encoded in a special way (much more memory efficient) when they
# have at max a given numer of elements, and the biggest element does not
# exceed a given threshold. You can configure this limits with the following
# configuration directives.
#redis 2.0后引入了 hash 数据结构。 
#hash 中包含超过指定元素个数并且最大的元素当没有超过临界时，hash 将以zipmap来存储
#zipmap又称为 small hash，可大大减少内存的使用
hash-max-zipmap-entries 512
hash-max-zipmap-value 64




# Active rehashing uses 1 millisecond every 100 milliseconds of CPU time in
# order to help rehashing the main Redis hash table (the one mapping top-level
# keys to values). The hash table implementation redis uses (see dict.c)
# performs a lazy rehashing: the more operation you run into an hash table
# that is rhashing, the more rehashing "steps" are performed, so if the
# server is idle the rehashing is never complete and some more memory is used
# by the hash table.
# 
# The default is to use this millisecond 10 times every second in order to
# active rehashing the main dictionaries, freeing memory when possible.
#
# If unsure:
# use "activerehashing no" if you have hard latency requirements and it is
# not a good thing in your environment that Redis can reply form time to time
# to queries with 2 milliseconds delay.
#
# use "activerehashing yes" if you don't have such hard requirements but
# want to free memory asap when possible.
#是否重置Hash表
#设置成yes后redis将每100毫秒使用1毫秒CPU时间来对redis的hash表重新hash，可降低内存的使用
#当使用场景有较为严格的实时性需求,不能接受Redis时不时的对请求有2毫秒的延迟的话，把这项配置为no。
#如果没有这么严格的实时性要求,可以设置为 yes,以便能够尽可能快的释放内存
activerehashing yes
```

##### Redis官方文档对VM的使用提出了一些建议:

当key很小而value很大时,使用VM的效果会比较好.因为这样节约的内存比较大, 当key不小时,可以考虑使用一些非常方法将很大的key变成很大的value,如可将key,value组合成一个新的value, 最好使用linux ext3 等对稀疏文件支持比较好的文件系统保存你的swap文件, vm-max-threads参数可设置访问swap文件的线程数，最好不要超过机器的核数；设置为0则所有对swap文件的操作都是串行的，可能会造成比较长时间的延迟,但是对数据完整性有很好的保证


##### redis数据存储

redis的存储分为内存存储、磁盘存储和log文件三部分，配置文件中有三个参数对其进行配置。


save seconds updates，save配置，指出在多长时间内，有多少次更新操作，就将数据同步到数据文件。可多个条件配合，默认配置了三个条件。


appendonly yes/no ，appendonly配置，指出是否在每次更新操作后进行日志记录，如果不开启，可能会在断电时导致一段时间内的数据丢失。因为redis本身同步数据文件是按上面的save条件来同步的，所以有的数据会在一段时间内只存在于内存中。


appendfsync no/always/everysec ，appendfsync配置，no表示等操作系统进行数据缓存同步到磁盘，always表示每次更新操作后手动调用fsync()将数据写到磁盘，everysec表示每秒同步一次。
