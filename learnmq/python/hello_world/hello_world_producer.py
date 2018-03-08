# -*- coding: utf-8 -*-

# Hello World Producer
# 发布者代码需要完成如下任务
# 1. 连接到RabbitMQ
# 2. 获取信道
# 3. 声明交换器
# 4. 创建消息
# 5. 发布消息
# 6. 关闭信道
# 7. 关闭连接
# Requires: pika >= 0.10.0
# Author: knitmesh

import pika
import sys


# 1. 建立到代理服务器的连接
credentials = pika.PlainCredentials("guest", "guest")
conn_params = pika.ConnectionParameters("localhost",
                                        credentials=credentials)
conn_broker = pika.BlockingConnection(conn_params)

# 2. 获取信道
channel = conn_broker.channel()

# 3. 声明交换器
channel.exchange_declare(exchange="hello-exchange",
                         type="direct",
                         passive=False,
                         durable=True,
                         auto_delete=False)
msg = sys.argv[1]
msg_props = pika.BasicProperties()

# 4. 创建一个纯文本消息
msg_props.content_type = "text/plain"

# 5. 发布消息
channel.basic_publish(body=msg,
                      exchange="hello-exchange",
                      properties=msg_props,
                      routing_key="hola")
# 并不需要每次都关闭信道和连接, 可通过一个信道持续的发送消息
