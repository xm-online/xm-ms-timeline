package com.icthh.xm.ms.timeline.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Converter
public class MapToStringConverter implements AttributeConverter<Map<String, Object>, String> {

    private ObjectMapper mapper = new ObjectMapper();

    public MapToStringConverter() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public String convertToDatabaseColumn(Map<String, Object> data) {
        try {
            Object obj = data != null ? data : new HashMap<>();
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Error during JSON to String converting", e);
            return Strings.EMPTY;
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String data) {
        try {
            String json = StringUtils.isNoneBlank(data) ? data : "{}";
            return mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (IOException e) {
            log.warn("Error during String to JSON converting", e);
            return Collections.emptyMap();
        }
    }

}
