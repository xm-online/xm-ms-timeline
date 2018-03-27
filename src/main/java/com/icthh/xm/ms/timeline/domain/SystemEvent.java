package com.icthh.xm.ms.timeline.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
public class SystemEvent {

    private String eventId;
    private String messageSource;
    private String tenantKey;
    private String userLogin;
    private String eventType;
    @JsonIgnore
    private Instant startDate = Instant.now();
    private Map<String, String> data = new HashMap<>();

    @JsonProperty("startDate")
    public String getStartDate() {
        return startDate.toString();
    }

}
