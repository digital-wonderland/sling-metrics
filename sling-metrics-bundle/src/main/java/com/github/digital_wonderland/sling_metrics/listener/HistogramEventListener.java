package com.github.digital_wonderland.sling_metrics.listener;

import com.codahale.metrics.Histogram;
import com.github.digital_wonderland.sling_metrics.service.MetricService;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.event.EventUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype = false, immediate = true)
@Service(value = EventHandler.class)
@Property(name="event.topics", value = "metric/histograms", propertyPrivate = true)
public class HistogramEventListener implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HistogramEventListener.class);

    @Reference
    protected MetricService metricService;

    @Override
    public void handleEvent(final Event event) {
        if(EventUtil.isLocal(event) && metricService.isEnabled()) {
            final String name = (String) event.getProperty(MetricEvent.NAME);
            final String value = (String) event.getProperty(MetricEvent.VALUE);

            if(StringUtils.isNotEmpty(name)) {
                Histogram histogram = metricService.getRegistry().histogram(name);
                try {
                    histogram.update(Long.parseLong(value));
                } catch(NumberFormatException e) {
                    LOG.error("Received histogram metric event without value: [{}] - {}", e.getMessage(), e);
                }
            } else {
                LOG.warn("Received metric event without name: [{}]", event);
            }
        }
    }
}
