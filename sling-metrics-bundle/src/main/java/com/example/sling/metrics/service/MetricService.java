package com.example.sling.metrics.service;

import com.codahale.metrics.MetricRegistry;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(name = "Metrics Service", metatype = true)
@Service(value = MetricService.class)
public class MetricService {

    private static final Logger LOG = LoggerFactory.getLogger(MetricService.class);

    @Property(label = "Enabled", boolValue = true, description = "Should the metrics service be enabled")
    private static final String METRIC_SERVICE_ENABLED = "metricService.enabled";

    private MetricRegistry registry;
    private boolean isEnabled;

    @Activate
    protected void activate(ComponentContext context) {
        isEnabled = (Boolean) context.getProperties().get(METRIC_SERVICE_ENABLED);
        if(isEnabled) {
            registry = new MetricRegistry();
            LOG.debug("New MetricRegistry created");
        }
    }

    public MetricRegistry getRegistry() {
        return registry;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
