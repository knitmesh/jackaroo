
https://yq.aliyun.com/articles/42950

mysql 分区

1.range分区
```
CREATE TABLE
IF NOT EXISTS USER (
	id INT NOT NULL auto_increment,
	name varchar(30) not null default '',
	sex int (1) NOT NULL DEFAULT '0',
	PRIMARY KEY (id)
) DEFAULT charset = utf8 auto_increment = 1 PARTITION BY RANGE (id)(
	PARTITION p0
	VALUES
		less than (3),
		PARTITION p1
	VALUES
		less than (6),
		PARTITION p2
	VALUES
		less than (9),
		PARTITION p3
	VALUES
		less than (12),
		PARTITION p4
	VALUES
		less than MAXVALUE
);
insert into test.user(name,sex)values ('tom14','1');
select count(id) as count from user;
```
从 information_schema 系统库中的 partitions 表中查看分区信息
```
select * from information_schema.partitions where table_schema='test' and table_name='user';
```

从某个分区中查询数据
```
select * from test.user partition(p0);
```
删除分区
```
alter table user drop partition p4; 
```
新增分区
如果分区中有最大分区 less than MAXVALUE 添加是不成功的
```
alter table test.user add partition (partition p5 values less than (20)); 添加分区成功

alter table test.user add partition (partition p6 values less than (MAXVALUE));
```

/*可以对现有表进行分区,并且会按規则自动的将表中的数据分配相应的分区 
中，这样就比较好了，可以省去很多事情，看下面的操作*/  
```
mysql> alter table aa partition by RANGE(id)  
 -> (PARTITION p1 VALUES less than (1),  
 -> PARTITION p2 VALUES less than (5),  
 -> PARTITION p3 VALUES less than MAXVALUE);  
Query OK, 15 rows affected (0.21 sec)   //对15数据进行分区  
Records: 15  Duplicates: 0  Warnings: 0  

alter table aa drop partition p2;  
Query OK, 0 rows affected (0.30 sec)  
Records: 0  Duplicates: 0  Warnings: 0  
```
只有11条了，说明对现有的表分区成功了  
