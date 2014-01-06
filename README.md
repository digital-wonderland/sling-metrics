Sling Metrics [![Build Status](https://api.travis-ci.org/digital-wonderland/sling-metrics.png)](https://travis-ci.org/digital-wonderland/sling-metrics)
=============

Integration of Coda Hales [Metrics](http://metrics.codahale.com/) with [Apache Sling](http://sling.apache.org/) and [Adobe CQ](http://www.adobe.com/sea/products/cq.html).

Goal
====

Create a tool that monitors & measures all interesting things in Sling / CQ out of the box and can be easily leveraged to measure / monitor a custom application running on top of one of them.

Features
========

The plugin consists of two separate bundles:

* sling-metrics-bundle (Sling/JVM/... - WIP)
* cq-metrics-bundle (CQ specific - TBD)

sling-metrics-bundle
--------------------

provides the general functionality and sling specific things:

* a central ```MetricRegistry``` is made available via an OSGi ```MetricService```.

    This registry also can be used to register custom metrics.

* several listeners register to topics prefixed with ```metric/``` to allow creation of metrics via OSGi events (not all metrics available yet).

* a central ```HealthCheckRegistry``` is made available via an OSGi ```HealthCheckService```.

    This registry also can be used to register custom health checks.

* reporters for SLF4J, Graphite & JMX get registered and can be configured via their respective OSGi configuration

* general [JVM metrics](http://metrics.codahale.com/manual/jvm/) are made available

* Sling proxies around [Metrics' servlets](http://metrics.codahale.com/manual/servlets/) (Health Check, Metrics, Ping & Thread dump)

* Sling specific metrics (TBD)

cq-metrics-bundle
-----------------

can be deployed on top of that and provides CQ specific checks & metrics (TBD - e.g. replication queue health check / metrics, ...).

3rd party applications
----------------------

By following the same approach custom metrics for additional applications can be brought in on top of that.

This way custom applications can generate whatever metrics they wish while avoiding compile time dependecies (publishing a metric is just generating an OSGi event) and or develop their own metrics bundles, which, when deployed, provide more insight by registering whatever metrics sets to the global metrics registry.


Graphite
--------

For your convenience there is a [Vagrant](http://vagrantup.com) config included providing a Graphite installation at [http://localhost:8080](http://localhost:8080). The respective Carbon instance is listening to ```33.33.33.12``` port ```2003```.

The Vagrant box is based on [this CentOS Packer template](https://github.com/digital-wonderland/packer-templates/tree/master/CentOS-6-x86_64) (The box is just a minimal CentOS 6 installation so anything similar should do as well).


Installation to CQ
====================

The metrics plugin can be easily installed into an existing CQ installation by using the ```server-package-deploy``` profile of the ```cq-metrics-package``` submodule. The CQ instance can be specified via the ```cq.server``` property.

E.g. to install to [http://localhost:4502](http://localhost:4502):

```
mvn -f cq-metrics-package/pom.xml install -Pserver-package-deploy -Dcq.server=http://localhost:4502
```

Now the different components can be configured via their [respective OSGi configuration](http://localhost:4502/system/console/configMgr)

Installation Pitfalls
---------------------

Coda Hales Metrics accesses ```sun.misc.Unsafe``` which has to be made explicitely accessible by adding the following to your ```sling.properties```:

```
org.osgi.framework.system.packages.extra=sun.misc
```
To make this change effective CQ has to be restarted.

License
=======

Copyright &copy; 2013-2014 Stephan Kleine

Published under Apache Software License 2.0, see LICENSE
