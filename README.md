Sling Metrics [![Build Status](https://api.travis-ci.org/digital-wonderland/sling-metrics.png)](https://travis-ci.org/digital-wonderland/sling-metrics)
=============

[Sling](http://sling.apache.org/) integration with Coda Hales [Metrics](http://metrics.codahale.com/).

Installation
============

Coda Hales Metrics accesses ```sun.misc.Unsafe``` which has to be made explicitely accessible by adding the following to your ```sling.properties```:

```
org.osgi.framework.system.packages.extra=sun.misc
```

License
=======

Copyright (c) 2013-2014 Stephan Kleine

Published under Apache Software License 2.0, see LICENSE
