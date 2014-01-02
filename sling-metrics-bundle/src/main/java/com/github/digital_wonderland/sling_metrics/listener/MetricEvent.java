package com.github.digital_wonderland.sling_metrics.listener;

import org.osgi.service.event.Event;

import java.util.Dictionary;

public class MetricEvent extends Event {

    public static final String NAME = "name";
    public static final String VALUE = "value";

    public MetricEvent(final String topic, final Dictionary properties) {
        super(topic, properties);
    }

    public String getName() {
        return (String) getProperty(NAME);
    }

    public String getValue() {
        return (String) getProperty(VALUE);
    }
}
