package com.icthh.xm.ms.timeline.service.dto;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.util.StringJoiner;

public class HeaderValueDeserializer extends ValueDeserializer<String> {

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext context) throws JacksonException {
        if (jsonParser.currentToken() == JsonToken.START_ARRAY) {
            StringJoiner joiner = new StringJoiner(", ");
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                joiner.add(jsonParser.getValueAsString());
            }
            return joiner.toString();
        }
        return jsonParser.getValueAsString();
    }
}
