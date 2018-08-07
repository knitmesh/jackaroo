---
layout: pattern
title: Mute Idiom
folder: mute-idiom
permalink: /patterns/mute-idiom/
pumlid: JSf13iCm20NHgxG7iDdtDjH62PKX5luarq-MtSsJvgtUHdR96AyTcEj357pLJR7dDvT4EnpYgEqmqf4NWuD-V7BfidJpCXcGy4N6wmcoX1Jj-lo2ziUQONMcZHi0
categories: Other
tags: 
 - Java
 - Difficulty-Beginner
 - Idiom
---

## Intent
Provide a template to suppress any exceptions that either are declared but cannot occur or should only be logged;
while executing some business logic. The template removes the need to write repeated `try-catch` blocks.


![alt text](./etc/mute-idiom.png "Mute Idiom")

## Applicability
Use this idiom when

* an API declares some exception but can never throw that exception eg. ByteArrayOutputStream bulk write method.
* you need to suppress some exception just by logging it, such as closing a resource.

## Credits

* [JOOQ: Mute Design Pattern](http://blog.jooq.org/2016/02/18/the-mute-design-pattern/)
