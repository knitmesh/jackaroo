## 一．MC适用场景
业务上对数据的要求满足如下方面的场景才适用。
---------------------------------------

**数据变化不频繁但访问频繁**

**无用户关联性**

**有访问通用性**

**数据安全要求低**

**数据一致性要求低**

**无持久化要求**

例如秒杀、抢购、抽奖活动的商品或礼品库存计数递减的场景，  
此场景对数据的可靠一致性要求很高，放在mc不可靠，很有可能被置换出去。

## 二．缓存key设置
1. key值最大长度250个字符，尽可能设置短些，不能设置太长，如果太大浪费内存。

2. key值不能有空格和控制字符等特殊符号，如\n  \r，最好不要使用中文，如使用了中文要key作MD5。

3. key值需按业务逻辑设置足够短后再MD5，不过key转MD5后需要实现获取key的功能，以便于清理mc或排查故障时需要拿到key。

4. 按业务逻辑来设置key，合理设置缓存key的粒度，以便提高命中率，比如产品终端页面的某个豆腐块是根据产品小类或品牌等出数据的，应用小类id或品牌id为基础设置key，而不是无脑地使用产品id为基础设置key。

## 三．缓存value值设置
1. value过期时间默认最大30天，如设置过期时间大于30天,值会设置不进缓存。

2. value最大能存储1MB，如果数据大于1MB可以考虑压缩或拆分到多个key中，在应用于页面级的缓存时要特别注意。

3. value最好转为string来保存。

4. 不要多重嵌套mc缓存。

## 四．缓存过期时间设置
（一）失效时间设置的方法

有两种：

1. 相对时间：多长时间，给出过期的时间长度

    如：memcache.set(key,value,new Date(5000))设置为5秒过期

2. 绝对时间：到期时间，给出过期的最后期限

    如：memcache.set(key,value,new Date(System.currentTimeMillis()+5000))设置为5秒过期

**当部署应用的服务器系统时间跟mc服务端的时间有差距时，使用绝对时间就有问题了，所以建议大家设置mc缓存过期时间时都用相对时间。看下面mc服务端源码就明了了。**

服务端的处理

时间处理源代码【memcached.c】如下：
```
#define REALTIME_MAXDELTA 60*60*24*30                     //定义30天的秒数

static rel_time_t realtime(const time_t exptime) {

if (exptime == 0) return 0;

if (exptime > REALTIME_MAXDELTA) {                       //超过30天，是绝对时间

if (exptime <= process_started)                         //小于进程启动日期

return (rel_time_t)1;                                  //

return (rel_time_t)(exptime - process_started);   //返回进程启动之后的时间差

} else {                                                                   //不超过30天，是相对时间

return (rel_time_t)(exptime + current_time);       // exptime + (tvsec - process_started)

}

}
```
相对时间的返回的值是：服务器当前时间之后的exptime - process_started秒

绝对时间的返回的值是：服务器当前时间之后的(exptime -服务端当前时间) - process_started秒

可以看到，如果Client和Server时间不一致，根据经验mc服务端跑着跑着其服务端时间通常会比系统时间慢 当然偶尔也会快，如使用绝对时间会有问题，如mc时间比系统时间慢时对一些过期时间有严格要求的就不会按时过期了，如果快了那缓存几乎很快都过期，之前摄影部落某台mc就是出现这种情况，在访问量大些时由于一大部分缓存集体失效导致数据库负载高。

所以综上所述使用相对时间是比较安全可靠的做法。

（二）缓存时长的设置策略

缓存失效时间设置加个随机数来控制，把失效时间打散，尽可能避免访问高峰期缓存集体过期而雪崩，比如某个页面块的缓存时间设置4小时*随机数，即是这个页面块每份缓存过期时间不一样。

获取随机数的参考代码：
```
//随机算法把缓存过期时间打散

privatefloatgetRandomFloat(){

        floatrandomFloat = 0;

         while(true){

                    randomFloat =newDouble(Math.random()).floatValue();

                   if(randomFloat>0){

                              if(randomFloat<0.5){

                                      randomFloat = randomFloat*2;

                               }

                           break;

                        }

               }

           returnrandomFloat;

}
```
## 五．一些重要的参数设置
首先保证同一应用各台服务器上的mc配置文件一致。

其他一些需要注意注意的参数：

- memcached.failover=false

    设置容错开关，设置为true，当前socket不可用时，程序会自动查找可用连接并返回，否则返回NULL。默认为true，最好设置为false，跟failback设置为true搭配使用，这样当某台mc宕机或其他问题时让该台的请求直接落到到数据库，当恢复后重新使用，这样就不影响整个mc集群的缓存key的重新分配，引发集群短期的动荡。

- memcached.failback=true

    设置连接失败恢复开关,设置为true，当宕机的服务器启动或中断的网络连接后，这个socket连接还可继续使用，否则将不再使用，默认是true，最好设置为true。

## 六．一些排查故障的方法
1. 用命令查看分析mc的内存使用、命中率和mc时间情况

    一般运维同事都有安装部署xmem和xmcstat命令。

    如：xmem  192.168.123.123 11211
    
    ![](http://upload-images.jianshu.io/upload_images/4245942-d671c166a7876cef.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/239)
    
    分析各台mc的Usage和HitRate的值，如果Usage值80%以上时有效期内缓存的置换率会比较高，所以缓存命中率相对会低些，这时留意加资源。

    如发现某台HitRate的值比其他的要低，该台mc很有可能有问题。

    xmcstat --i="192.168.123.123:11211"
    
    ![](http://upload-images.jianshu.io/upload_images/4245942-938282ab10753802.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/700)

    **重点留意MC时间和服务器系统时间这两个时间，如果mc时间比服务器时间快很多，如果我们设置缓存过期时间是绝对时间的，那肯定会降低缓存的命中率，所以设置缓存过期时间要用相对时间。**

2. 如发现一些mc值没过期的情况下灵异消失，一般是某台mc有问题或内存使用80%以上时被置换出去了。排查某台mc有问题可以telnet上去每台mc手动get某个key来看看，当然要先要知道该key是hash到那台mc。