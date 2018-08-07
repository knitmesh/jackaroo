---
layout: pattern
title: Double Checked Locking
folder: double-checked-locking
permalink: /patterns/double-checked-locking/
pumlid: TSdH4SCW203GLTe1bFzkGv1J6qGFeLc_MI1_x-wzkv94uJ1vDVUrFm26LwxTMnonsMYgitgcEQ1BNEXeyCKVfiAxLqqBtTbqmy1z0ygCGpXHOpgv99bqTgt0JW-LmqPUCUGF
categories: Concurrency
tags:
 - Java
 - Difficulty-Beginner
 - Idiom
---

## Intent
Reduce the overhead of acquiring a lock by first testing the
locking criterion (the "lock hint") without actually acquiring the lock. Only
if the locking criterion check indicates that locking is required does the
actual locking logic proceed.

![alt text](./etc/double_checked_locking_1.png "Double Checked Locking")

## Applicability
Use the Double Checked Locking pattern when

* there is a concurrent access in object creation, e.g. singleton, where you want to create single instance of the same class and checking if it's null or not maybe not be enough when there are two or more threads that checks if instance is null or not.
* there is a concurrent access on a method where method's behaviour changes according to the some constraints and these constraint change within this method.
