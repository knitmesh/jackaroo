# mysql

关于mysql的相关技术贴；

### mysql性能
```
Mysql性能优化的一些技巧
 包括合理索引、sql语句命中、结构、底层架构等
```

### show variables和show status
```
可以使用show variables和show status 命令可以查看mysql服务的静态参数值和动态运行状态信息。
```

### EXPLAIN
```
sql分析函数 EXPLAIN
```

### php的mysql时间戳
```
1970年以前的时间戳处理
    1970年以前的时间戳处理，存储在数据库是负数；
    对应php解决方法
```

### BTREE、哈希索引
```
特点和不同
```

### processList 注意要点

### 分库分表分布式数据库开发，全局唯一ID解决方案

### InnoDB解析
```
    索引
    锁
    事务
```    

### HA下MySQL的一些配置优化

```
    1.记录慢速查询
    2.对查询进行缓存
    3.强制限制
```

### MySQL中文乱码

    将数据库编码统一 uft8
    特殊的比如头像字段，可以用utf8mb4（因为一些表情的存储输出超出了原本utf8的三位存储空间，变成了4位。另外需要讨论的）

### 索引相关--BTREE
    
    BTREE及其相关概念