---
layout: pattern
title: Aggregator Microservices
folder: aggregator-microservices
permalink: /patterns/aggregator-microservices/
pumlid: JOov3SCm301NIGQGs7iRXYPa1g8ayB7NjuiKwGvtmBrbKC-Tq_hhY5Y-0HXUjKaS-Kbdepc2HrIQ2jBpma23BvvOTdPfeooCO1iEYlu0O6l63MDQKI6Rp-CKOWSE-ey_NzEqhjH-0m00
categories: Architectural
tags:
- Java
- Spring
---

## Intent

The user makes a single call to the Aggregator, and the aggregator then calls each relevant microservice and collects
the data, apply business logic to it, and further publish is as a REST Endpoint.
More variations of the aggregator are: 
- Proxy Microservice Design Pattern: A different microservice is called upon the business need. 
- Chained Microservice Design Pattern: In this case each microservice is dependent/ chained to a series 
of other microservices.

![alt text](./etc/aggregator-microservice.png "Aggregator Microservice")

## Applicability

Use the Aggregator Microservices pattern when you need a unified API for various microservices, regardless the client device.

## Credits

* [Microservice Design Patterns](http://blog.arungupta.me/microservice-design-patterns/)
