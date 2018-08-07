---
layout: pattern
title: Double Dispatch
folder: double-dispatch
permalink: /patterns/double-dispatch/
pumlid: NSbB3iCW303HgpG70Ezx6yTOWSeOv4zp_MRTtUZDCPGa6wV9gqTiVmCOtlKQqVDCPwEbmHgLreGXUMEWmGU_M1hxkBHiZ61JXud-1BILft1fmvz37JZetshQh3kd_000
categories: Other
tags:
 - Java
 - Difficulty-Intermediate
 - Idiom
---

## Intent
Double Dispatch pattern is a way to create maintainable dynamic
behavior based on receiver and parameter types.

![alt text](./etc/double-dispatch.png "Double Dispatch")

## Applicability
Use the Double Dispatch pattern when

* the dynamic behavior is not defined only based on receiving object's type but also on the receiving method's parameter type.

## Real world examples

* [ObjectOutputStream](https://docs.oracle.com/javase/8/docs/api/java/io/ObjectOutputStream.html)
