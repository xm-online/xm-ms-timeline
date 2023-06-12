package com.icthh.xm.ms.timeline.web.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ElasticsearchSourceFilterDto {

    private List<String> includes = List.of();
    private List<String> excludes = List.of();

}
