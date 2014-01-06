package com.github.digital_wonderland.sling_metrics.servlets;

import com.github.digital_wonderland.sling_metrics.service.MetricService;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

@SlingServlet(paths = "/bin/sling-metrics/metrics", methods = "GET")
public class MetricsServlet extends com.codahale.metrics.servlets.MetricsServlet {

    @Reference
    private MetricService metricService;

    private ServletConfig servletConfig;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        servletConfig = config;
        servletConfig.getServletContext().setAttribute(METRICS_REGISTRY, metricService.getRegistry());
        super.init(servletConfig);
    }

    @Deactivate
    protected void deactivate() {
        servletConfig.getServletContext().removeAttribute(METRICS_REGISTRY);
    }

}
