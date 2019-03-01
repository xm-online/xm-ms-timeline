package com.icthh.xm.ms.timeline.domain.properties;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Filter {

    @JsonProperty("excludeMethod")
    private List<String> excludeMethod = new ArrayList<>();
}
