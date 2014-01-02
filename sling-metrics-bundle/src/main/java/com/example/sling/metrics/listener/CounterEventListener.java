package com.example.sling.metrics.listener;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.example.sling.metrics.service.MetricService;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.event.EventUtil;
import org.apache.sling.event.jobs.JobProcessor;
import org.apache.sling.event.jobs.JobUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedMap;

@Component(metatype = false, immediate = true)
@Service(value = EventHandler.class)
@Property(name="event.topics", value = "metric/counters", propertyPrivate = true)
public class CounterEventListener implements EventHandler, JobProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(CounterEventListener.class);

    @Reference
    protected MetricService metricService;

    @Override
    public void handleEvent(final Event event) {
        if (EventUtil.isLocal(event)) {
            JobUtil.processJob(event, this);
        }
    }

    @Override
    public boolean process(final Event event) {
        final String name = (String) event.getProperty(MetricEvent.NAME);
        final String value = (String) event.getProperty(MetricEvent.VALUE);

        if(metricService.isEnabled()) {
            if(StringUtils.isNotEmpty(name)) {
                if(StringUtils.isEmpty(value)) {
                    try {
                        final long l = Long.parseLong(value);
                        final Counter counter = metricService.getRegistry().counter(name);
                        if(0 < l) {
                            counter.inc(l);
                        } else {
                            counter.dec(l);
                        }
                    } catch(NumberFormatException e) {
                        LOG.error(e.getMessage(), e);
                    }
                } else {
                    LOG.warn("Received counter metric event without value: [{}]", event);
                }
            } else {
                LOG.warn("Received metric event without name: [{}]", event);
            }
        }

        return true;
    }

}
