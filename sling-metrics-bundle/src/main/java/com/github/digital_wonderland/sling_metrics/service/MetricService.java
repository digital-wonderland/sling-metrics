package com.github.digital_wonderland.sling_metrics.service;

import com.codahale.metrics.*;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component(label = "Sling Metrics :: Metric Service", metatype = true)
@Service(value = MetricService.class)
public class MetricService {

    private static final Logger LOG = LoggerFactory.getLogger(MetricService.class);

    @Property(label = "Enabled", boolValue = true, description = "Should the metrics service be enabled")
    private static final String METRIC_SERVICE_ENABLED = "metricService.enabled";

    @Property(label = "Global Metric Prefix", description = "Leave empty to use 'sling.metrics.$hostname|$ip'")
    private static final String METRIC_PREFIX = "metricService.metricPrefix";

    private MetricRegistry registry;
    private boolean isEnabled;
    private String metricPrefix;

    @Activate
    protected void activate(final ComponentContext context) {
        isEnabled = (Boolean) context.getProperties().get(METRIC_SERVICE_ENABLED);
        metricPrefix = (String) context.getProperties().get(METRIC_PREFIX);
        metricPrefix = metricPrefix == null ? "" : metricPrefix.trim();
        if(metricPrefix.isEmpty()) {
            final String tmpl = "sling.metrics.%s";
            try {
                metricPrefix = String.format(tmpl, InetAddress.getLocalHost().getHostName());
            } catch (UnknownHostException e) {
                LOG.warn("Unable to determine hostname", e);
                try {
                    metricPrefix = String.format(tmpl, InetAddress.getLocalHost().getHostAddress());
                } catch (UnknownHostException e1) {
                    LOG.warn("Unable to determine ip", e1);
                    metricPrefix = "sling.metrics";
                }
            }
        }
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

    public String getMetricPrefix() { return metricPrefix; }

    /**
     * Concatenates elements to form a dotted name, eliding any null values or empty strings.
     *
     * @param name     the first element of the name
     * @param names    the remaining elements of the name
     * @return {@code name} and {@code names} concatenated by periods
     * @see com.codahale.metrics.MetricRegistry#name
     */
    public String name(String name, String... names) {
        return com.codahale.metrics.MetricRegistry.name(String.format("%s.%s", metricPrefix, name), names);
    }

    /**
     * Given a {@link Metric}, registers it under the given name.
     *
     * @param name   the name of the metric
     * @param metric the metric
     * @param <T>    the type of the metric
     * @return {@code metric}
     * @throws IllegalArgumentException if the name is already registered
     * @see com.codahale.metrics.MetricRegistry#register
     */
    public <T extends Metric> T register(String name, T metric) throws IllegalArgumentException {
        return registry.register(name(name), metric);
    }

    /**
     * Removes the metric with the given name.
     *
     * @param name the name of the metric
     * @return whether or not the metric was removed
     * @see com.codahale.metrics.MetricRegistry#remove
     */
    public boolean remove(String name) { return registry.remove(name(name)); }

    /**
     * Creates a new {@link Meter} and registers it under the given name.
     *
     * @param name the name of the metric
     * @return a new {@link Meter}
     * @see com.codahale.metrics.MetricRegistry#meter
     */
    public Meter meter(String name) { return registry.meter(name(name)); }

    /**
     * Creates a new {@link Histogram} and registers it under the given name.
     *
     * @param name the name of the metric
     * @return a new {@link Histogram}
     * @see com.codahale.metrics.MetricRegistry#histogram
     */
    public Histogram histogram(String name) { return registry.histogram(name(name)); }

    /**
     * Creates a new {@link Counter} and registers it under the given name.
     *
     * @param name the name of the metric
     * @return a new {@link Counter}
     * @see com.codahale.metrics.MetricRegistry#counter
     */
    public Counter counter(String name) { return registry.counter(name(name)); }

    /**
     * Creates a new {@link Timer} and registers it under the given name.
     *
     * @param name the name of the metric
     * @return a new {@link Timer}
     * @see com.codahale.metrics.MetricRegistry#timer
     */
    public Timer timer(String name) { return registry.timer(name(name)); }
}
