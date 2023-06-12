package com.icthh.xm.ms.timeline.template;

import com.icthh.xm.ms.timeline.web.dto.ElasticsearchSourceFilterDto;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TemplateParamsHolder {
    private Map<String, String> templateParams = new HashMap<>();
    private ElasticsearchSourceFilterDto sourceFilter;
}
