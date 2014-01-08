package com.github.digital_wonderland.sling_metrics.filter;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.github.digital_wonderland.sling_metrics.service.MetricService;
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

    @Property(label = "Component Whitelist", value = ".*", description = "Regular expression to filter events by white listing on the resource type")
    private static final String COMPONENT_FILTER_WHITELIST= "componentFilter.whitelist";

    @Reference
    protected MetricService metricService;

    private boolean isEnabled = false;

    private String whitelist = ".*";

    @Activate
    protected void activate(final ComponentContext context) {
        isEnabled = (Boolean) context.getProperties().get(COMPONENT_FILTER_ENABLED);
        whitelist = (String) context.getProperties().get(COMPONENT_FILTER_WHITELIST);
        LOG.debug("ComponentFilter.isEnabled: [{}]", isEnabled);
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        LOG.debug("ComponentFilter init()");
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        LOG.debug("ComponentFilter doFilter()");

        if(isEnabled && metricService.isEnabled()) {
            if(servletRequest instanceof SlingHttpServletRequest) {
                final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) servletRequest;
                final String resourceType = slingRequest.getResource().getResourceType();
                final String metricName = normalizeResourceType(resourceType);
                if(metricName.matches(whitelist)) {
                    final MetricRegistry registry = metricService.getRegistry();
                    final Timer.Context context = registry.timer(metricName).time();
                    try {
                        filterChain.doFilter(servletRequest, servletResponse);
                    } finally {
                        context.stop();
                    }
                }
            } else {
                LOG.error("Metrics ComponentFilter got called for non SlingHttpServletRequest");
            }
        }
    }

    private String normalizeResourceType(final String resourceType) {
        return ("components." + resourceType).replaceAll("[\\./:]+", ".");
    }

    @Override
    public void destroy() {
        LOG.debug("ComponentFilter destroy()");
    }
}
