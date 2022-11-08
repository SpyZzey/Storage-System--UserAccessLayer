package de.storagesystem.api.calendar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EventType {
    APPOINTMENT(1),
    BIRTHDAY(2),
    MEETING(3);

    private int value;

    EventType(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static EventType of(Integer value) {
        if(value == null) return null;

        for (EventType eventType : EventType.values()) {
            if (eventType.getValue() == value) {
                return eventType;
            }
        }
        throw new IllegalArgumentException("No enum constant for value " + value);
    }

}
