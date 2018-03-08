# -*- coding: utf-8 -*-

# Hello World Producer
# 发布者代码需要完成如下任务
# 1. 连接到RabbitMQ
# 2. 获取信道
# 3. 声明交换器
# 4. 声明队列
# 5. 把队列和交换器绑定起来
# 6. 消费消息
# 7. 关闭信道
# 8. 关闭连接
# Requires: pika >= 0.10.0
# Author: knitmesh

import pika

credentials = pika.PlainCredentials("guest", "guest")
conn_params = pika.ConnectionParameters("localhost",
                                        credentials=credentials)

# 1. 建立倒代理服务器的连接
conn_broker = pika.BlockingConnection(conn_params)

# 2. 获取信道
channel = conn_broker.channel()

# 3. 声明交换器, exchange_declare方法语义为如果不存在该交换器就创建
channel.exchange_declare(exchange="hello-exchange",
                         type="direct",
                         passive=False,
                         durable=True,
                         auto_delete=False)

# 4. 声明队列
channel.queue_declare(queue="hello-queue")

# 5. 通过键"hola"将队列和交换器绑定起来
channel.queue_bind(queue="hello-queue",
                   exchange="hello-exchange",
                   routing_key="hola")


# 6. 处理消息的回调函数函数
def msg_consumer(channel, method, header, body):
    # 7. 确认消息
    channel.basic_ack(delivery_tag=method.delivery_tag)

    if body == "quit":
        # 8. 停止消费并退出,
        channel.basic_cancel(consumer_tag="hello-consumer")
        # 9. 关闭信道和连接
        channel.stop_consuming()
    else:
        print body

    return

# 10. 消费者订阅队列
channel.basic_consume(msg_consumer,
                      queue="hello-queue",
                      consumer_tag="hello-consumer")
# 11. 开始消费
channel.start_consuming()
