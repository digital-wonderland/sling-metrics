package com.github.digital_wonderland.sling_metrics.servlets;

import org.apache.felix.scr.annotations.sling.SlingServlet;

@SlingServlet(paths = "/bin/sling-metrics/ping", methods = "GET")
public class PingServlet extends com.codahale.metrics.servlets.PingServlet {

}
