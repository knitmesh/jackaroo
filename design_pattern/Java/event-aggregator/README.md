---
layout: pattern
title: Event Aggregator
folder: event-aggregator
permalink: /patterns/event-aggregator/
pumlid: PSf13iCW30NHgxG70Ezx6uTOX0eCih-JwvTzTwEdUJSjFKu9wwyBMFuXCdvoRRZY21ShKo6ANEQWrkDXiD6NRqwdUAkQ5WDYwZJOTv3SUqzSgqbbp0qeVvZ3Hbun-Wy0
categories: Structural
tags:
 - Java
 - Difficulty-Beginner
 - Reactive
---

## Intent
A system with lots of objects can lead to complexities when a
client wants to subscribe to events. The client has to find and register for
each object individually, if each object has multiple events then each event
requires a separate subscription. An Event Aggregator acts as a single source
of events for many objects. It registers for all the events of the many objects
allowing clients to register with just the aggregator.

![alt text](./etc/classes.png "Event Aggregator")

## Applicability
Use the Event Aggregator pattern when

* Event Aggregator is a good choice when you have lots of objects that are
  potential event sources. Rather than have the observer deal with registering
  with them all, you can centralize the registration logic to the Event
  Aggregator. As well as simplifying registration, a Event Aggregator also
  simplifies the memory management issues in using observers.

## Credits

* [Martin Fowler - Event Aggregator](http://martinfowler.com/eaaDev/EventAggregator.html)
