package com.icthh.xm.ms.timeline.service.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    disableSubMappingMethodsGeneration = true
)
public interface BaseMapper {

}
