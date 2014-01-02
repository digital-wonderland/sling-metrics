package com.example.sling.metrics.filter;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.example.sling.metrics.service.MetricService;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingFilterScope;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

@SlingFilter(scope = SlingFilterScope.COMPONENT,
        order = -10000,
        metatype = true,
        name = "Metrics Component Filter",
        description = "Sling Components Filter which generates Metrics for Components")
public class ComponentFilter implements javax.servlet.Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentFilter.class);

    @Property(label = "Enabled", boolValue = true, description = "Should the filter be enabled")
    private static final String COMPONENT_FILTER_ENABLED = "componentFilter.enabled";

    @Reference
    protected MetricService metricService;

    private boolean isEnabled = false;

    @Activate
    protected void activate(final ComponentContext context) {
        isEnabled = (Boolean) context.getProperties().get(COMPONENT_FILTER_ENABLED);
        LOG.info("ComponentFilter.isEnabled: [{}]", isEnabled);
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        LOG.info("ComponentFilter init()");
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        LOG.debug("ComponentFilter doFilter()");

        if(isEnabled && metricService.isEnabled()) {
            final MetricRegistry registry = metricService.getRegistry();
            if(servletRequest instanceof SlingHttpServletRequest) {
                final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) servletRequest;
                final String resourceType = slingRequest.getResource().getResourceType();
                final Timer.Context context = registry.timer(resourceType).time();
                try {
                    filterChain.doFilter(servletRequest, servletResponse);
                } finally {
                    context.stop();
                }
            } else {
                LOG.error("Metrics ComponentFilter got called for non SlingHttpServletRequest");
            }
        }
    }

    @Override
    public void destroy() {
        LOG.info("ComponentFilter destroy()");
    }
}
