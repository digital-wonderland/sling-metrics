package com.github.digital_wonderland.sling_metrics.servlets;

import com.github.digital_wonderland.sling_metrics.service.HealthCheckService;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

@SlingServlet(paths = "/bin/sling-metrics/healthcheck", methods = "GET")
public class HealthCheckServlet extends com.codahale.metrics.servlets.HealthCheckServlet {

    @Reference
    private HealthCheckService healthCheckService;

    private ServletConfig servletConfig;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        servletConfig = config;
        servletConfig.getServletContext().setAttribute(HEALTH_CHECK_REGISTRY, healthCheckService.getRegistry());
        super.init(servletConfig);
    }

    @Deactivate
    protected void deactivate() {
        servletConfig.getServletContext().removeAttribute(HEALTH_CHECK_REGISTRY);
    }

}
