package com.github.digital_wonderland.sling_metrics.servlets;

import org.apache.felix.scr.annotations.sling.SlingServlet;

@SlingServlet(paths = "/bin/sling-metrics/threads", methods = "GET")
public class ThreadDumpServlet extends com.codahale.metrics.servlets.ThreadDumpServlet {

}
