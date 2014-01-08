package com.github.digital_wonderland.sling_metrics.reporter;

import com.github.digital_wonderland.sling_metrics.service.MetricService;
import org.apache.felix.scr.annotations.*;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(name = "Metrics JMX Reporter", metatype = true)
public class JmxReporter {

    private static final Logger LOG = LoggerFactory.getLogger(JmxReporter.class);

    @Property(label = "Enabled", boolValue = false, description = "Should the JMX reporter be enabled")
    private static final String JMX_REPORTER_ENABLED = "jmxReporter.enabled";

    @Reference
    private MetricService metricService = null;

    private com.codahale.metrics.JmxReporter reporter = null;

    @Activate
    protected void activate(final ComponentContext context) {
        final boolean isEnabled = (Boolean) context.getProperties().get(JMX_REPORTER_ENABLED);
        LOG.info("Metrics JmxReporter is enabled: [{}]", isEnabled);

        if(isEnabled && metricService.isEnabled()) {
            reporter = com.codahale.metrics.JmxReporter.forRegistry(metricService.getRegistry()).build();
            reporter.start();
        }
    }

    @Deactivate
    protected void deactivate() {
        if(reporter != null) {
            reporter.stop();
            LOG.info("Metrics JmxReporter stopped");
        }
    }

}
