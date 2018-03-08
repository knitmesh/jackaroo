# -*- coding: utf-8 -*-
###############################################
# RabbitMQ in Action
# Chapter 1 - Hello World Producer
#             w/ Publisher Confirms
# 
# Requires: pika >= 0.9.6
# 
# Author: Jason J. W. Williams
# (C)2011
###############################################

import pika
import sys
from pika import spec

credentials = pika.PlainCredentials("guest", "guest")
conn_params = pika.ConnectionParameters("localhost",
                                        credentials = credentials)
conn_broker = pika.BlockingConnection(conn_params) 

channel = conn_broker.channel()


# 1. 发送方确认处理方法
def confirm_handler(frame):
    if type(frame.method) == spec.Confirm.SelectOk:
        print "Channel in 'confirm' mode."
    elif type(frame.method) == spec.Basic.Nack:
        if frame.method.delivery_tag in msg_ids:
            print "Message lost!"
    elif type(frame.method) == spec.Basic.Ack:
        if frame.method.delivery_tag in msg_ids:
            print "Confirm received!"
            msg_ids.remove(frame.method.delivery_tag)

# 2. 把信道设置为发送方确认模式
channel.confirm_delivery(callback=confirm_handler)

msg = sys.argv[1]
msg_props = pika.BasicProperties()
msg_props.content_type = "text/plain"

# 3. 定义ID追踪列表
msg_ids = []

# 4. 发布消息
channel.basic_publish(body=msg,
                      exchange="hello-exchange",
                      properties=msg_props,
                      routing_key="hola")

# 5. 将ID添加至追踪列表
msg_ids.append(len(msg_ids) + 1)
channel.close()
