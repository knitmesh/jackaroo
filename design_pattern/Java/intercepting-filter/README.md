---
layout: pattern
title: Intercepting Filter
folder: intercepting-filter
permalink: /patterns/intercepting-filter/
pumlid: RSfB3i8m303Hgy014k-vZN5DQkIuaJ_q-fGzkz7JtCL8Q-DolUsPAnu0ZcSVadizAzZfi6JBJiS4qJenqU6D7smRXmnh2pFPBM1YN05o_KwyKcoqb-ZFEEcVz_BPLqtz0W00
categories: Behavioral
tags:
 - Java
 - Difficulty-Intermediate
---

## Intent
Provide pluggable filters to conduct necessary pre-processing and
post-processing to requests from a client to a target
 
![alt text](./etc/intercepting-filter.png "Intercepting Filter")
 
## Applicability
Use the Intercepting Filter pattern when

* a system uses pre-processing or post-processing requests
* a system should do the authentication/ authorization/ logging or tracking of request and then pass the requests to corresponding handlers 
* you want a modular approach to configuring pre-processing and post-processing schemes

## Real world examples

* [javax.servlet.FilterChain](https://tomcat.apache.org/tomcat-8.0-doc/servletapi/javax/servlet/FilterChain.html) and [javax.servlet.Filter](https://tomcat.apache.org/tomcat-8.0-doc/servletapi/javax/servlet/Filter.html)
* [Struts 2 - Interceptors](https://struts.apache.org/docs/interceptors.html)

## Credits

* [TutorialsPoint - Intercepting Filter](http://www.tutorialspoint.com/design_pattern/intercepting_filter_pattern.htm)
* [Presentation Tier Patterns](http://www.javagyan.com/tutorials/corej2eepatterns/presentation-tier-patterns)
