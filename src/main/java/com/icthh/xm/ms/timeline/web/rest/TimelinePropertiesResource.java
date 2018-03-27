package com.icthh.xm.ms.timeline.web.rest;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.icthh.xm.ms.timeline.domain.properties.TenantProperties;
import com.icthh.xm.ms.timeline.service.TenantPropertiesService;
import com.icthh.xm.ms.timeline.web.rest.vm.TimeLineValidationVM;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "timelines")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class TimelinePropertiesResource {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    private final TenantPropertiesService tenantPropertiesService;

    @PostMapping(value = "/timelines/properties/validate", consumes = {TEXT_PLAIN_VALUE})
    @ApiOperation(value = "Validate timeline properties format", response = TimeLineValidationVM.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Timeline validation result", response = TimeLineValidationVM.class),
        @ApiResponse(code = 500, message = "Internal server error")})
    @SneakyThrows
    @Timed
    @PreAuthorize("hasPermission(null, 'TIMELINE.TENANT.PROPERTIES.VALIDATE')")
    public TimeLineValidationVM validate(@RequestBody String timelineYml) {
        try {
            mapper.readValue(timelineYml, TenantProperties.class);
            return TimeLineValidationVM.builder().isValid(true).build();
        } catch (JsonParseException | JsonMappingException e) {
            log.error("Error while validation", e);
            return TimeLineValidationVM.builder().isValid(false).errorMessage(e.getLocalizedMessage()).build();
        }
    }

    @PostMapping(value = "/timelines/properties", consumes = {TEXT_PLAIN_VALUE})
    @ApiOperation(value = "Update timeline properties", response = ResponseEntity.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Timeline properties update result", response = ResponseEntity.class),
        @ApiResponse(code = 500, message = "Internal server error")})
    @SneakyThrows
    @Timed
    @PreAuthorize("hasPermission({'timelineYml': #timelineYml}, 'TIMELINE.TENANT.PROPERTIES.UPDATE')")
    public ResponseEntity<Void> updateTimelineProperties(@RequestBody String timelineYml) {
        tenantPropertiesService.updateTenantProps(timelineYml);
        return ResponseEntity.ok().build();
    }

}
