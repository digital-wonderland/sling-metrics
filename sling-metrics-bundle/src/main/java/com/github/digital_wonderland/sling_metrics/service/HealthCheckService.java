package com.github.digital_wonderland.sling_metrics.service;

import com.codahale.metrics.health.HealthCheckRegistry;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(label = "Sling Metrics :: HealthCheck Service", metatype = true)
@Service(value = HealthCheckService.class)
public class HealthCheckService {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckService.class);

    @Property(label = "Enabled", boolValue = true, description = "Should the health check service be enabled")
    private static final String HEALTH_CHECK_SERVICE_ENABLED = "healthCheckService.enabled";

    private HealthCheckRegistry registry;
    private boolean isEnabled;

    @Activate
    protected void activate(final ComponentContext context) {
        isEnabled = (Boolean) context.getProperties().get(HEALTH_CHECK_SERVICE_ENABLED);
        if(isEnabled) {
            registry = new HealthCheckRegistry();
            LOG.debug("New HealthCheckRegistry created");
        }
    }

    public HealthCheckRegistry getRegistry() {
        return registry;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
