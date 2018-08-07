---
layout: pattern
title: Memento
folder: memento
permalink: /patterns/memento/
pumlid: DSgn4OCm30NGLM00h3xR2AC3SvRiaxx2-g59zugtDgiz3qdlomNC-10vF-Lik7BF4A_388PIXrBh-J3OwUOlRuT4EssR38XRa7Ay81Lz_o11_RkaQvcf_GS0
categories: Behavioral
tags:
 - Java
 - Gang Of Four
 - Difficulty-Intermediate
---

## Also known as
Token

## Intent
Without violating encapsulation, capture and externalize an
object's internal state so that the object can be restored to this state later.

![alt text](./etc/memento.png "Memento")

## Applicability
Use the Memento pattern when

* a snapshot of an object's state must be saved so that it can be restored to that state later, and
* a direct interface to obtaining the state would expose implementation details and break the object's encapsulation

## Real world examples

* [java.util.Date](http://docs.oracle.com/javase/8/docs/api/java/util/Date.html)

## Credits

* [Design Patterns: Elements of Reusable Object-Oriented Software](http://www.amazon.com/Design-Patterns-Elements-Reusable-Object-Oriented/dp/0201633612)
