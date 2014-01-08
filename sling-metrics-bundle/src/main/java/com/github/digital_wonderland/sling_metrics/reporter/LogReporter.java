package com.github.digital_wonderland.sling_metrics.reporter;

import com.codahale.metrics.Slf4jReporter;
import com.github.digital_wonderland.sling_metrics.service.MetricService;
import org.apache.felix.scr.annotations.*;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@Component(name = "Metrics Log Reporter", metatype = true)
public class LogReporter {

    private static final Logger LOG = LoggerFactory.getLogger(LogReporter.class);

    @Property(label = "Enabled", boolValue = false, description = "Should the log reporter be enabled")
    private static final String LOG_REPORTER_ENABLED = "logReporter.enabled";

    @Property(label = "Logger", value = "com.example.metrics", description = "The name of the logger to be used")
    private static final String LOG_REPORTER_LOGGER_NAME = "logReporter.logger";

    @Reference
    private MetricService metricService = null;

    private Slf4jReporter reporter = null;

    @Activate
    protected void activate(final ComponentContext context) {
        final boolean isEnabled = (Boolean) context.getProperties().get(LOG_REPORTER_ENABLED);
        LOG.info("Metrics LogReporter is enabled: [{}]", isEnabled);

        if(isEnabled && metricService.isEnabled()) {
            reporter = Slf4jReporter.forRegistry(metricService.getRegistry())
                    .outputTo(LoggerFactory.getLogger((String) context.getProperties().get(LOG_REPORTER_LOGGER_NAME)))
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .build();
            reporter.start(1, TimeUnit.MINUTES);
        }
    }

    @Deactivate
    protected void deactivate() {
        if(reporter != null) {
            reporter.stop();
            LOG.info("Metrics LogReporter stopped");
        }
    }

}
