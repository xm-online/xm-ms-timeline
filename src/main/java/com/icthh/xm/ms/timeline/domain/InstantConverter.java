package com.icthh.xm.ms.timeline.domain;

import java.sql.Timestamp;
import java.time.Instant;
import javax.persistence.AttributeConverter;

public class InstantConverter implements AttributeConverter<Instant, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(Instant instant) {
        if (instant == null) {
            return null;
        } else {
            return new Timestamp(instant.toEpochMilli());
        }
    }

    @Override
    public Instant convertToEntityAttribute(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        } else {
            return Instant.ofEpochMilli(timestamp.getTime());
        }
    }

}
