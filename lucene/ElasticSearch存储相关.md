### 1. 版本

线上使用的是1.7.1版本（http://nj02-bdg-recsys-online02.nj02.baidu.com:8083/），最新的ES版本是5.0，这个版本在功能和性能上做了很大的改进（Elasticsearch 5.0 新版本的特性与改进），所以我们也直接使用这个版本。

风险是这个版本相对比较新，是十一月中旬才release的版本，可能会有一些不稳定，资料也会比较少。另外，因为太新，很多第三方插件，如head，bigdesk，等，都还没有追上。
### 2. Nodes、Index、Type、Shards & Replicas
1. Index or Type

根据ES的特性 Index VS Type 和 General recommendations，不同垂类还是建立在不同的索引下比较合适。
2. nodes & shard number

关键在于每个shard能够服务多少数据？根据这个数据我们就可以确定需要的分片数。


#### 这里有一些经验数据：

1. 一个index的最佳文档数为500w左右，超过则性能会下降的比较厉害。（来自于 @炼钢。官方文档是2,147,483,519，其实跟机器性能有关系）。 

2. the maximum JVM heap size recommendation for Elasticsearch is approximately 30-32GB. This is a solid estimate on the limit of your absolute maximum shard size. （Optimizing Elasticsearch: How Many Shards per Index?）

另外比较蛋疼的是分片数是在索引创建的时候指定，然后就是固定的不能动态调整了（Index Modules），如果发现shard太多或者太少的问题，之前如果要设置Elasticsearch的分片数，只能在创建索引的时候设置好，并且数据进来了之后就不能进行修改，如果要修改，只能重建索引。

所以一种做法就是根据数据的增加预估，设置比较一个比较大的分片数。但是分片数设置过多也会带来一些问题：

* 影响查询性能。因为需要跨多个分片请求、合并数据(可以通过routing避免，不过需要有一个route key，对业务有要求)，虽然是并行请求各个shards，不过如果shards > nodes，那么一个节点就需要串行服务多个请求，这也会影响到合并过程。分片数实际上也跟服务节点数目有关系，因为shard要均匀分布在各个node上。性能最好当然是one shard per node，但是为了后面的scale out，一般会配置shards多一点，推荐的系数是1.5~3倍，例如有3个nodes，那么最多分片3*3=9个shards，不过跟动态调大分片数不同，ES的节点是可以动态加入的，然后会自动重新分布shards。


综上所述，分片数是需要仔细权衡的。

* http://stackexchange.com/performance，StackOverflow使用了3台192GB RAM的SSD机器支撑528GB的索引大小。不过不知道人家的分片数。

## TIPS

### 1. Shrink API

ES5.0提供了一个Shrink API，可以将分片数进行收缩成原来的因数，比如之前是15个分片，那么可以收缩成5个或者3个又或者1个。这个能力特别适用于下面这种应用场景：在写入压力非常大的收集阶段，设置足够多的分片数，充分利用shard的并行写能力，索引写完之后收缩成更少的shard，提高查询性能。

具体参见：Shrink Index

### 2. Reindex API

因为各种设置原因（比如上面的分片数调整），经常需要重建索引，数据源在各种场景，重建起来很是头痛，ES5.0新加了Reindex接口。它可以直接在Elasticsearch集群里面对数据进行重建，如果你的mapping因为修改而需要重建，又或者索引设置修改需要重建的时候，借助Reindex可以很方便的异步进行重建，并且支持跨集群间的数据迁移。

具体参见: Reindex API

一期上线数据规模是：

Docs count: 185,251,838，其中商品数据占了90%。 Size: 472.3 gb

每天的增量大概是12w左右。以后抓取性能提高，可能会增加到50w左右。一年后大概翻一翻。

根据这个数据，对ES的集群规划和设置如下：

集群规模：Nodes = 3~5台，SSD的话最好。 内存大小：可用内存 > 64GB。 Index & Shards: 每个垂类单独一个index，分片数根据不同的垂类大小设置。比如数据量小的垂类，像景点，可以设置 2 primary shard + 1 replicas， 数据量大的垂类，像商品，可以设置20 primary shards + 1 replicas。 然后一开始可以设置大一些(翻倍)，全量灌库完成之后，再使用Shrink API进行缩减。
### 3. Replica number

严格来说，shards分为两种类型：primary shard 和 replica shard。primary shard就是我们前面讨论的，服务于所有的读写服务。而replica shard则是primary shard的备份，当主分片挂掉的时候顶上去。Replica主要有如下两个作用：

    主分片的备份，当主分片挂掉的时候顶上去。

    提供读服务，提升读性能。

相对来说，replica number并不会太难确定，因为他是只读的，可以动态调整。所以一般来说可以先设置为1，后续跟进需要调整。设置过大会浪费磁盘空间，增加同步压力。

### 4. dynamic mapping、default mapping、index templates & dynamic templates

动态mapping既是ES的优点，同时也是一把双刃剑。不需要创建索引，定义mapping，ES默认会根据文档进行类型推动，然后每种类型都有默认的一个行为（分词 & 索引）。比如默认ES会对所有的String字段使用标准分词器(standard-analyzer)进行分词(analyzed)，索引(indexed)，并且将他们加入到_all字段。这个并不是完全适用，比如tags字段，就不合适分词。

所以一般需要定制化。

首先可以控制是否允许动态创建索引——禁止Automatic Index Creation：
```
// Automatic index creation can include a pattern based white/black list, 
// for example, set action.auto_create_index to +aaa*,-bbb*,+ccc*,-* (+ meaning allowed, and - meaning disallowed).
action.auto_create_index = -kg*
```
然后可以控制是否允许动态类型映射——Put Mapping：
```
// Automatic mapping creation can be disabled by setting index.mapper.dynamic to false per-index as an index setting.
index.mapper.dynamic = false
```
TIPS 如何动态配置全局配置
```
PUT _template/template_all
{
  "template": "*",
  "order":0,
  "settings": {
    "index.mapper.dynamic": false 
  }
}
```
然后还有如下这些方式自定义mapping规则：
```
    default mapping：更新默认的mapping行为

        will be used as the base mapping for any new mapping types

        While the default mapping can be updated after an index has been created, the new defaults will only affect mapping types that are created afterwards.

        The default mapping can be used in conjunction with Index templates to control dynamically created types within automatically created indices

    Dynamic field mappings: 控制动态字段发现的规则

        By default, when a previously unseen field is found in a document, Elasticsearch will add the new field to the type mapping.

        可以控制是否动态发现类型，匹配的格式（如日期发现），等等

    Dynamic templates: 利用自定义规则来配置动态添加的字段的映射

        基于某些动态的规则动态的映射，如字段名字，字段类型。对于有很多字段的mapping，可以避免大量枯燥的mapping字段定义。

    Index templates: allow you to configure the default mappings, settings and aliases for new indices, whether created automatically or explicitly.

        包括 settings 和 mappings，已经匹配的索引名称。
```
这些方式并不是互斥的，而是可以组合的，比如下面模板就结合了default mapping & index templates & dynamic templates三种方式：
```
PUT _template/logging
{
  "template":   "logs-*", 
  "settings": { "number_of_shards": 1 }, 
  "mappings": {
    "_default_": {
      "_all": { 
        "enabled": false
      },
      "dynamic_templates": [
        {
          "strings": { 
            "match_mapping_type": "string",
            "mapping": {
              "type": "string",
              "fields": {
                "raw": {
                  "type":  "string",
                  "index": "not_analyzed",
                  "ignore_above": 256
                }
              }
            }
          }
        }
      ]
    }
  }
}
```
对于KG情况，所有的Index以 kg_ 作为前缀。page_entity以 kg_pe_ 开头。与GI保持一致，覆盖默认的动态mapping。对我们这种情况，默认所有的string不分词。只针对name, alias, tag进行索引。综上，我们可以这样子配置我们的mapping:
```
PUT _template/kg_template
{
   "template": "kg_*",
   "mappings": {
      "_default_": {
         "properties": {
            "name": {
               "type": "keyword"
            },
            "aliases": {
               "type": "keyword"
            },
            "tag": {
               "type": "keyword"
            }
         },
         "dynamic_templates": [
            {
               "unindexed_string": {
                  "match": "*",
                  "match_mapping_type": "string",
                  "mapping": {
                     "index": "no"
                  }
               }
            }
         ]
      }
   }
}
```
### 5. Multiple Indices and Index Aliases

ES的很多API都支持同时操作多个索引了，包括wildcards，比如kg_*。

具体参见：Multiple Indices。

可以利用ES的Index Aliases 避免索引名变化需要修改应用程序。

ES的Index Aliases功能其实比想象中的强大，它还可以支持一对多的映射，这样可以对多个垂类进行统一名称访问（当然，这个别名就不能用于写入，只能用于查询）：
```
POST /_aliases
{
    "actions" : [
        { "add" : { "index" : "kg_*", "alias" : "kg" } },
        { "add" : { "index" : "kg_pe_*", "alias" : "kg_pe" } }
    ]
}
```
ES的Index Aliases还可以支持对某个index的某个查询建立别名，类似于数据库的视图，例如下面语句建立了一个JD商品的索引别名：
```
POST /_aliases
{
    "actions" : [
        {
            "add" : {
                 "index" : "kg_product",
                 "alias" : "kg_product_jd",
                 "filter" : { "term" : { "source" : "jd" } }
            }
        }
    ]
}
```
ES的别名不会递归替换，也就是只会解释第一层的alias，所以不用担心别名会被二次替换。
### 6. 上线环境设置

1、JDK版本

最少1.7，推荐1.8。

2、JVM heap size:

ES推荐的最大的JVM heap size大概是30~32GB。

export ES_HEAP_SIZE=28g

3、禁止swap
```
/etc/sysctl.conf:

vm.swappiness = 1

elasticsearch.yml:

bootstrap.mlockall: true

4、File Descriptors and MMap

/etc/sysctl.conf:

fs.file-max = 2097152
vm.max_map_count = 262144

/etc/security/limits.conf

*         hard    nofile      500000
*         soft    nofile      500000
root      hard    nofile      500000
root      soft    nofile      500000
```
### 7. 设置单播集群通讯模式

ES有两种方式构建集群：

    Multicast

    Unicast

相对来说，Unicast不容易出错，特别是跨网段的时候。下面是一个例子：
```
node.name: "kg_es_production"
discovery.zen.ping.multicast.enabled: false
discovery.zen.ping.unicast.hosts: ["node-1.example.com", "node-2.example.com", "node-3.example.com"]
discovery.zen.minimum_master_nodes: 2
```
其中，设置 discovery.zen.minimum_master_nodes =2 是一种防止脑裂的方式，为了保证这个配置生效，我们需要准备奇数个节点(odd number of nodes)，然后把这个值设置为ceil(num_of_nodes / 2)。对于上面的配置，最多可以失去一个节点。这个方式很像 quorum in Zookeeper。

### 8. 减少不必要的索引

包括这些：

    使用正确的索引方式，不要使用默认的动态mapping，对每个字段的指明是否需要分词，是否需要索引。

    For search-only fields, set store to false.

    Disable _all field, if you always know which field to search.

    Disable _source fields, if documents are big and you don’t need the update capability.

    If you have a document key, set this field in _id - path, instead of index the field twice.

    提高index.refresh_interval的值(默认是1s)，如果不需要near-realtime search。这个选项对于提高初始化灌库性能也是非常有用的。
