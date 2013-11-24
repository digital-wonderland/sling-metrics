package com.example.sling.metrics.listener;

import org.osgi.service.event.Event;

import java.util.Dictionary;

public class MetricEvent extends Event {

    public static final String NAME = "name";
    public static final String VALUE = "value";

    public MetricEvent(String topic, Dictionary properties) {
        super(topic, properties);
    }

    public String getName() {
        return (String) getProperty(NAME);
    }

    public String getValue() {
        return (String) getProperty(VALUE);
    }
}
