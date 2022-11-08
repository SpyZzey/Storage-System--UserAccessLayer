package de.storagesystem.api.calendar;

import org.springframework.stereotype.Component;
import org.springframework.core.convert.converter.Converter;

@Component
public class EventTypeConverter implements Converter<String, EventType> {
    @Override
    public EventType convert(String value) {
        return EventType.of(Integer.valueOf(value));
    }

}
