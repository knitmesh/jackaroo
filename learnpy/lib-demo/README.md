1. APScheduler 轻量的定时任务库

- [docs](http://apscheduler.readthedocs.io/en/latest/)

- [demo](./APScheduler.md)

2. futurist openstack开源的异步功能库

- [docs](https://docs.openstack.org/futurist/latest/)

- [demo](./futurist.md)

3. greenlet 提供可自行调度的‘微线程’, 既协程

- [docs](http://greenlet.readthedocs.io/en/latest/)

- [demo](./greenlet.md)

4. eventlet

    eventlet是一个用来处理和网络相关的python库函数，且可以通过协程（coroutines）实现并发。在eventlet里，将协程叫做greenthread(绿色线程)，所谓并发，即开启多个greenthread，并对这些greenthread进行管理。尤为方便的是，eventlet为了实现“绿色线程”，竟然对python的和网络相关的几个标准库函数进行了改写，并且可以以补丁（patch）的方式导入到程序中，因为python的库函数只支持普通的线程，而不支持协程，eventlet称之为“绿化”。
    eventlet主要基于两个库——greenlet（过程化其并发基础，简单封装后即成为GreenTread）和select.epoll（默认网络通信模型）。

- [docs](http://eventlet.net/doc/)

- [demo](./eventlet.md)