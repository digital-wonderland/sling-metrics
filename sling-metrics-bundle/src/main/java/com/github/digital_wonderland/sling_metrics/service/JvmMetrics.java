package com.github.digital_wonderland.sling_metrics.service;

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
    private static final String JVM_METRICS_ENABLED = "jvmMetrics.enabled";

    @Reference
    protected MetricService metricService;

    private boolean isEnabled;

    @Activate
    protected void activate(final ComponentContext context) {
        isEnabled = (Boolean) context.getProperties().get(JVM_METRICS_ENABLED);
        if(metricService.isEnabled() && isEnabled) {
            metricService.register(GARBAGE_COLLECTION_METRIC_NAME, new GarbageCollectorMetricSet());
            metricService.register(MEMORY_METRIC_NAME, new MemoryUsageGaugeSet());
            metricService.register(THREAD_STATES_METRIC_NAME, new ThreadStatesGaugeSet());
            metricService.register(FILE_DESCRIPTORS_USAGE_METRIC_NAME, new FileDescriptorRatioGauge());
        }
    }

    @Deactivate
    protected void deactivate() {
        if(metricService.isEnabled() && isEnabled) {
            metricService.remove(GARBAGE_COLLECTION_METRIC_NAME);
            metricService.remove(MEMORY_METRIC_NAME);
            metricService.remove(THREAD_STATES_METRIC_NAME);
            metricService.remove(FILE_DESCRIPTORS_USAGE_METRIC_NAME);
        }
    }

}
