package com.example.sling.metrics.reporter;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.graphite.Graphite;
import com.example.sling.metrics.service.MetricService;
import org.apache.felix.scr.annotations.*;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Dictionary;
import java.util.concurrent.TimeUnit;

@Component(name = "Metrics Graphite Reporter", metatype = true)
public class GraphiteReporter {

    private static final Logger LOG = LoggerFactory.getLogger(GraphiteReporter.class);

    @Property(label = "Enabled", boolValue = false, description = "Should the log reporter be enabled")
    private static final String GRAPHITE_REPORTER_ENABLED = "graphiteReporter.enabled";

    @Property(label = "Hostname", value = "localhost", description = "The hostname where Graphite is running")
    private static final String GRAPHITE_REPORTER_HOST_NAME = "graphiteReporter.hostname";

    @Property(label = "Port", intValue = 2003, description = "The port where Graphite is running")
    private static final String GRAPHITE_REPORTER_PORT = "graphiteReporter.port";

    @Property(label = "Prefix", value = "sling.metrics", description = "The prefix to be used in Graphite")
    private static final String GRAPHITE_REPORTER_PREFIX = "graphiteReporter.prefix";

    @Reference
    private MetricService metricService = null;

    private com.codahale.metrics.graphite.GraphiteReporter reporter = null;

    @Activate
    protected void activate(final ComponentContext context) {
        final Dictionary properties = context.getProperties();
        final boolean isEnabled = (Boolean) properties.get(GRAPHITE_REPORTER_ENABLED);
        LOG.info("Metrics GraphiteReporter is enabled: [{}]", isEnabled);

        if(isEnabled && metricService.isEnabled()) {
            final String hostname = (String) properties.get(GRAPHITE_REPORTER_HOST_NAME);
            final int port = (Integer) properties.get(GRAPHITE_REPORTER_PORT);
            final String prefix = (String) properties.get(GRAPHITE_REPORTER_PREFIX);
            final Graphite graphite = new Graphite(new InetSocketAddress(hostname, port));
            reporter = com.codahale.metrics.graphite.GraphiteReporter.forRegistry(metricService.getRegistry())
                    .prefixedWith(prefix)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .filter(MetricFilter.ALL)
                    .build(graphite);
            reporter.start(1, TimeUnit.MINUTES);
            LOG.info("Reporting metrics to Graphite @ [{}:{}] with prefix [{}]", new Object[] {hostname, port, prefix});
        }
    }

    @Deactivate
    protected void deactivate() {
        if(reporter != null) {
            reporter.stop();
            LOG.info("Metrics GraphiteReporter stopped");
        }
    }

}
