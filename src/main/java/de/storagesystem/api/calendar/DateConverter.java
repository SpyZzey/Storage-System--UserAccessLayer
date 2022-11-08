package de.storagesystem.api.calendar;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class DateConverter implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(String value) {
        return LocalDateTime.parse(value);
    }

}
