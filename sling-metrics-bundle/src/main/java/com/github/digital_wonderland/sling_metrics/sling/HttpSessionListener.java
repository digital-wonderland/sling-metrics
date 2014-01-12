package com.github.digital_wonderland.sling_metrics.sling;

import com.codahale.metrics.Counter;
import com.github.digital_wonderland.sling_metrics.service.MetricService;
import org.apache.felix.scr.annotations.*;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSessionEvent;

@Component(immediate = true, metatype = false)
@Service
public class HttpSessionListener implements javax.servlet.http.HttpSessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(HttpSessionListener.class);

    private static final String OPEN_SESSIONS_METRIC = "sessions";

    @Reference
    protected MetricService metricService;

    private Counter counter;

    @Activate
    protected void activate(final ComponentContext context) {
        counter = metricService.counter(OPEN_SESSIONS_METRIC);
    }

    @Deactivate
    protected void deactivate() {
        metricService.remove(OPEN_SESSIONS_METRIC);
        counter = null;
    }

    @Override
    public void sessionCreated(final HttpSessionEvent httpSessionEvent) {
        LOG.debug("HTTP session created");
        counter.inc();
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
        LOG.debug("HTTP session destroyed");
        counter.dec();
    }
}
