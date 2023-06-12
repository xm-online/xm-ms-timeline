package com.icthh.xm.ms.timeline.template;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "key")
@JsonPropertyOrder( {"key", "query", "msName", "aggregateType", "fields"})
public class Template implements Comparable<Template> {

    private String key;
    private String query;
    private String msName;
    private String aggregateType;
    private List<String> fields = new ArrayList<>();

    @Override
    public int compareTo(Template o) {
        return key.compareTo(o.getKey());
    }
}
