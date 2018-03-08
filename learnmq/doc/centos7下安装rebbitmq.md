1. 安装Erlang

    Erlang各系统版本安装(http://docs.basho.com/riak/1.3.0/tutorials/installation/Installing-Erlang/)

    以下展示Centos7 Erlang安装

    首先需要安装erlang，参考：http://fedoraproject.org/wiki/EPEL/FAQ#howtouse

        rpm -Uvh http://download.fedoraproject.org/pub/epel/7/x86_64/e/epel-release-7-8.noarch.rpm
        yum install erlang

    安装过程中会有提示，一路输入“y”即可。

2. 安装RabbitMQ：

    先下载rpm：

        wget http://www.rabbitmq.com/releases/rabbitmq-server/v3.6.6/rabbitmq-server-3.6.6-1.el7.noarch.rpm
    下载完成后安装：

        yum install rabbitmq-server-3.6.6-1.el7.noarch.rpm
    完成后启动服务：

        service rabbitmq-server start
    可以查看服务状态：

        service rabbitmq-server status

        [root@localhost rabbitmq]# service rabbitmq-server status
        Redirecting to /bin/systemctl status  rabbitmq-server.service
        ?.rabbitmq-server.service - RabbitMQ broker
           Loaded: loaded (/usr/lib/systemd/system/rabbitmq-server.service; disabled; vendor preset: disabled)
           Active: active (running) since Fri 2016-10-21 04:59:26 EDT; 7s ago
         Main PID: 38884 (beam)
           Status: "Initialized"
           CGroup: /system.slice/rabbitmq-server.service
                   ?..38884 /usr/lib64/erlang/erts-5.10.4/bin/beam -W w -A 64 -P 1048576 -t 5000000 -stbt db -zdbbl 32000 -K true -- -root /usr/lib64/erlang -progname erl -- -home /var/lib/rabbitmq -- -pa /usr/lib/rabbitm...
                   ?..39021 /usr/lib64/erlang/erts-5.10.4/bin/epmd -daemon
                   ?..39121 inet_gethost 4
                   ?..39122 inet_gethost 4

        Oct 21 04:59:25 localhost.localdomain rabbitmq-server[38884]: RabbitMQ 3.6.6. Copyright (C) 2007-2016 Pivotal Software, Inc.
        Oct 21 04:59:25 localhost.localdomain rabbitmq-server[38884]: ##  ##      Licensed under the MPL.  See http://www.rabbitmq.com/
        Oct 21 04:59:25 localhost.localdomain rabbitmq-server[38884]: ##  ##
        Oct 21 04:59:25 localhost.localdomain rabbitmq-server[38884]: ##########  Logs: /var/log/rabbitmq/rabbit@localhost.log
        Oct 21 04:59:25 localhost.localdomain rabbitmq-server[38884]: ######  ##        /var/log/rabbitmq/rabbit@localhost-sasl.log
        Oct 21 04:59:25 localhost.localdomain rabbitmq-server[38884]: ##########
        Oct 21 04:59:25 localhost.localdomain rabbitmq-server[38884]: Starting broker...
        Oct 21 04:59:26 localhost.localdomain rabbitmq-server[38884]: systemd unit for activation check: "rabbitmq-server.service"
        Oct 21 04:59:26 localhost.localdomain systemd[1]: Started RabbitMQ broker.
        Oct 21 04:59:26 localhost.localdomain rabbitmq-server[38884]: completed with 0 plugins.


    这里可以看到log文件的位置

        Logs: /var/log/rabbitmq/rabbit@localhost.log
    转到文件位置，打开文件：

        cat rabbit\@localhost.log
    这里显示的是没有找到配置文件

        =INFO REPORT==== 21-Oct-2016::04:59:25 ===
        node           : rabbit@localhost
        home dir       : /var/lib/rabbitmq
        config file(s) : /etc/rabbitmq/rabbitmq.config (not found)
        cookie hash    : l61RtwfvhkbvOO7PfHsF/Q==
        log            : /var/log/rabbitmq/rabbit@localhost.log
        sasl log       : /var/log/rabbitmq/rabbit@localhost-sasl.log
        database dir   : /var/lib/rabbitmq/mnesia/rabbit@localhost

     我们可以自己创建这个文件

        cd /etc/rabbitmq/
        vi rabbitmq.config
     编辑内容如下：

        [{rabbit, [{loopback_users, []}]}].

        这里的意思是开放使用，rabbitmq默认创建的用户guest，密码也是guest，这个用户默认只能是本机访问，localhost或者127.0.0.1，从外部访问需要添加上面的配置。
     保存配置后重启服务：

        service rabbitmq-server stop
        service rabbitmq-server start

    此时就可以从外部访问了，但此时再看log文件，发现内容还是原来的，还是显示没有找到配置文件，可以手动删除这个文件再重启服务，不过这不影响使用

        rm rabbit\@mythsky.log
        service rabbitmq-server stop
        service rabbitmq-server start

    开放5672端口：

        firewall-cmd --zone=public --add-port=5672/tcp --permanent
        firewall-cmd --reload

    开启管理UI：

        rabbitmq-plugins enable rabbitmq_management
        firewall-cmd --zone=public --add-port=15672/tcp --permanent
        firewall-cmd --reload

    在Windows下打开地址：


        http://10.255.19.111:15672

    用户名和密码都是 guest (需要配置rabbitmq.config)

    添加用户

        rabbitmqctl add_user admin 111111

    设置权限
        rabbitmqctl set_user_tags admin administrator
