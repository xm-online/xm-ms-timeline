package com.icthh.xm.ms.timeline.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.icthh.xm.ms.timeline.config.tenant.TenantInfo;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class SystemEvent {

    private String eventId;
    private String messageSource;
    private TenantInfo tenantInfo;
    private String eventType;
    @JsonIgnore
    private Instant startDate = Instant.now();
    private Map<String, String> data = new HashMap<>();

    @JsonProperty("startDate")
    public String getStartDate() {
        return startDate.toString();
    }

}
