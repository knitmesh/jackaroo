# -*- coding: utf-8 -*-
###############################################
# RabbitMQ in Action
# Chapter 1 - Hello World Producer
#             w/ Transactions
# 
# Requires: pika >= 0.9.5
# 
# Author: Jason J. W. Williams
# (C)2011
###############################################

import pika
import sys

credentials = pika.PlainCredentials("guest", "guest")
conn_params = pika.ConnectionParameters("localhost",
                                        credentials=credentials)
# 1. 建立到代理服务器的连接
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

channel.tx_select()
# 5. 发布消息
channel.basic_publish(body=msg,
                      exchange="hello-exchange",
                      properties=msg_props,
                      routing_key="hola")
channel.tx_commit()
