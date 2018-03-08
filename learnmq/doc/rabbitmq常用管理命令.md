1. 管理rabbitmq

    Rabbitmq服务器的主要通过rabbitmqctl和rabbimq-plugins两个工具来管理，以下是一些常用功能。

    1）.服务器启动与关闭

        启动: rabbitmq-server –detached
        关闭:rabbitmqctl stop
        若单机有多个实例，则在rabbitmqctlh后加–n 指定名称
    2）.插件管理

        开启某个插件：rabbitmq-pluginsenable xxx
        关闭某个插件：rabbitmq-pluginsdisablexxx
        注意：重启服务器后生效。
    3）.virtual_host管理

        新建virtual_host: rabbitmqctladd_vhost  xxx
        撤销virtual_host:rabbitmqctl  delete_vhost xxx
    4）. 用户管理

        新建用户：rabbitmqctl add_user xxxpwd
        删除用户:   rabbitmqctl delete_user xxx
        改密码: rabbimqctlchange_password {username} {newpassword}
        设置用户角色：rabbitmqctlset_user_tags {username} {tag ...}

        Tag可以为 administrator,monitoring, management
    5）. 权限管理

        权限设置：set_permissions [-pvhostpath] {user} {conf} {write} {read}
                 Vhostpath: Vhost路径
                 user: 用户名
                 Conf: 一个正则表达式match哪些配置资源能够被该用户访问。
                 Write: 一个正则表达式match哪些配置资源能够被该用户读。
                 Read:  一个正则表达式match哪些配置资源能够被该用户访问。
    6）. 获取服务器状态信息

         服务器状态：rabbitmqctl status
         队列信息：rabbitmqctl list_queues[-p vhostpath] [queueinfoitem ...]

                  Queueinfoitem可以为：
                  name，durable，auto_delete，arguments，messages_ready，
                  messages_unacknowledged，messages，consumers，memory


                  Exchange信息：rabbitmqctllist_exchanges[-p vhostpath] [exchangeinfoitem ...]

                  Exchangeinfoitem可以为：
                  name，type，durable，auto_delete，internal，arguments.


         Binding信息：rabbitmqctllist_bindings[-p vhostpath] [bindinginfoitem ...]

                  Bindinginfoitem可以为：
                  source_name，source_kind，destination_name，destination_kind，routing_key，arguments

         Connection信息：rabbitmqctllist_connections [connectioninfoitem ...]

                  Connectioninfoitem可以为：
                  recv_oct，recv_cnt，send_oct，send_cnt，send_pend等。
         Channel信息：rabbitmqctl  list_channels[channelinfoitem ...]

                  Channelinfoitem可以为:
                  consumer_count，messages_unacknowledged