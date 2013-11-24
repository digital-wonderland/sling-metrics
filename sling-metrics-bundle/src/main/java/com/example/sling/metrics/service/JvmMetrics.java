package com.example.sling.metrics.service;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import org.apache.felix.scr.annotations.*;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codahale.metrics.MetricRegistry.name;

@Component(name = "Metrics JVM Integration", metatype = true)
public class JvmMetrics {

    private static final Logger LOG = LoggerFactory.getLogger(MetricService.class);

    private static final String METRIC_NAME_PREFIX = "jvm";
    private static final String GARBAGE_COLLECTION_METRIC_NAME = name(METRIC_NAME_PREFIX, "gc");
    private static final String MEMORY_METRIC_NAME = name(METRIC_NAME_PREFIX, "memory");
    private static final String THREAD_STATES_METRIC_NAME = name(METRIC_NAME_PREFIX, "thread-states");
    private static final String FILE_DESCRIPTORS_USAGE_METRIC_NAME = name(METRIC_NAME_PREFIX, "fd", "usage");

    @Property(label = "Enabled", boolValue = true, description = "Should JVM metrics get collected")
    private static final String METRIC_SERVICE_ENABLED = "jvmService.enabled";

    @Reference
    protected MetricService metricService;

    private boolean isEnabled;

    @Activate
    protected void activate(final ComponentContext context) {
        isEnabled = (Boolean) context.getProperties().get(METRIC_SERVICE_ENABLED);
        if(metricService.isEnabled() && isEnabled) {
            final MetricRegistry metricRegistry = metricService.getRegistry();
            metricRegistry.register(GARBAGE_COLLECTION_METRIC_NAME, new GarbageCollectorMetricSet());
            metricRegistry.register(MEMORY_METRIC_NAME, new MemoryUsageGaugeSet());
            metricRegistry.register(THREAD_STATES_METRIC_NAME, new ThreadStatesGaugeSet());
            metricRegistry.register(FILE_DESCRIPTORS_USAGE_METRIC_NAME, new FileDescriptorRatioGauge());
        }
    }

    @Deactivate
    protected void deactivate() {
        if(metricService.isEnabled() && isEnabled) {
            final MetricRegistry metricRegistry = metricService.getRegistry();
            metricRegistry.remove(GARBAGE_COLLECTION_METRIC_NAME);
            metricRegistry.remove(MEMORY_METRIC_NAME);
            metricRegistry.remove(THREAD_STATES_METRIC_NAME);
            metricRegistry.remove(FILE_DESCRIPTORS_USAGE_METRIC_NAME);
        }
    }

}
