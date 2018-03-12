WSGI, Web Server Gateway Interface
如全称代表的那样，WSGI不是服务器，不是API，不是Python模块，更不是什么框架，而是一种服务器和客户端交互的接口规范！更具体的规范说明请搜索“PEP 3333”。
在WSGI规范下，web组件被分成三类：client, server, and middleware.
WSGI apps(服从该规范的应用)能够被连接起来(be stacked)处理一个request，
这也就引发了中间件这个概念，中间件同时实现c端和s端的接口，c看它是上游s，s看它是下游的c。
WSGI的s端所做的工作仅仅是接收请求，传给application（做处理），然后将结果response给middleware或client.
除此以外的工作都交给中间件或者application来做。

apache+mod_wsgi+django，django是一个web框架，但也可以看成就是一个复杂一点的输入用户请求，返回页面的函数，并且满足WSGI里面的规定，
而mod_wsgi就是apache的一个模块，这个模块让apache这个静态服务器可以帮满足WSGI规范的函数实现获取用户请求和输出页面的功能。

