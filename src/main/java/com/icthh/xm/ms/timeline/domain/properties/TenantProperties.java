package com.icthh.xm.ms.timeline.domain.properties;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "filter" })
@Data
public class TenantProperties {

    @JsonProperty("filter")
    private Filter filter;

    @JsonProperty("event")
    private Event event;

    public static class Event {

        private Boolean hidePayload = true;

        @JsonGetter("hidePayload")
        public Boolean getHidePayload() {
            return hidePayload;
        }

        @JsonSetter("hidePayload")
        public void setHidePayload(Boolean hidePayload) {
            this.hidePayload = hidePayload;
        }

        // TODO FIXME remove after fix all config in prod
        @JsonSetter("hide-payload")
        public void setHidePayloadAlias(Boolean hidePayload) {
            this.hidePayload = hidePayload;
        }
    }

}
