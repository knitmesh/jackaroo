---
layout: pattern
title: Business Delegate
folder: business-delegate
permalink: /patterns/business-delegate/
pumlid: POl13SCm3CHMQGU8zUysgYCuBcJ5a4x9-l6_Fu84tzsgvYxf-Zg06HyYvxkqZYE_6UBrD8YXr7DGrxmPxFJZYxTTeZVR9WFY5ZGu5j2wkad4wYgD8IIe_xQaZp9pw0C0
categories: Business Tier
tags:
 - Java
 - Difficulty-Intermediate
---

## Intent
The Business Delegate pattern adds an abstraction layer between
presentation and business tiers. By using the pattern we gain loose coupling
between the tiers and encapsulate knowledge about how to locate, connect to,
and interact with the business objects that make up the application.

![alt text](./etc/business-delegate.png "Business Delegate")

## Applicability
Use the Business Delegate pattern when

* you want loose coupling between presentation and business tiers
* you want to orchestrate calls to multiple business services
* you want to encapsulate service lookups and service calls

## Credits

* [J2EE Design Patterns](http://www.amazon.com/J2EE-Design-Patterns-William-Crawford/dp/0596004273/ref=sr_1_2)
