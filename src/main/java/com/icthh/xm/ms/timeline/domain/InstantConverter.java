package com.icthh.xm.ms.timeline.domain;

import javax.persistence.AttributeConverter;
import java.sql.Timestamp;
import java.time.Instant;

public class InstantConverter implements AttributeConverter<Instant, Timestamp> {


    @Override
    public Timestamp convertToDatabaseColumn(Instant instant) {
        return new Timestamp(instant.toEpochMilli());
    }

    @Override
    public Instant convertToEntityAttribute(Timestamp aLong) {
        return Instant.ofEpochMilli(aLong.getTime());
    }
}
