package com.github.digital_wonderland.sling_metrics.sling;

import com.github.digital_wonderland.sling_metrics.service.MetricService;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingConstants;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

@Component(immediate = true, metatype = false)
@Service
@Properties({
        @Property(name = EventConstants.EVENT_TOPIC, value = { SlingConstants.TOPIC_RESOURCE_ADDED,
                SlingConstants.TOPIC_RESOURCE_CHANGED,
                SlingConstants.TOPIC_RESOURCE_REMOVED}),
        @Property(name = EventConstants.EVENT_FILTER, value = "(path=/*)")
})
public class JcrResourceEventListener implements EventHandler {

    @Reference
    private MetricService metricService;

    @Override
    public void handleEvent(Event event) {
        final String topic = event.getTopic();
        if (SlingConstants.TOPIC_RESOURCE_ADDED.equals(topic)) {
            metricService.meter("jcr.resource.added").mark();
        } else if (SlingConstants.TOPIC_RESOURCE_CHANGED.equals(topic)) {
            metricService.meter("jcr.resource.changed").mark();
        } else if (SlingConstants.TOPIC_RESOURCE_REMOVED.equals(topic)) {
            metricService.meter("jcr.resource.removed").mark();
        }
    }
}
