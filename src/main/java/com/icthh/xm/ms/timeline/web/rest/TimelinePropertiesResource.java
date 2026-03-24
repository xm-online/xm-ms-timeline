package com.icthh.xm.ms.timeline.web.rest;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.icthh.xm.commons.permission.annotation.PrivilegeDescription;
import com.icthh.xm.ms.timeline.domain.properties.TenantProperties;
import com.icthh.xm.ms.timeline.service.TenantPropertiesService;
import com.icthh.xm.ms.timeline.web.rest.vm.TimeLineValidationVM;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "timelines", description = "Timeline Properties API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class TimelinePropertiesResource {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    private final TenantPropertiesService tenantPropertiesService;

    @PostMapping(value = "/timelines/properties/validate", consumes = {TEXT_PLAIN_VALUE})
    @Operation(summary = "Validate timeline properties format", description = "Validate timeline YAML configuration")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Timeline validation result",
            content = @Content(schema = @Schema(implementation = TimeLineValidationVM.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")})
    @SneakyThrows
    @Timed
    @PreAuthorize("hasPermission(null, 'TIMELINE.TENANT.PROPERTIES.VALIDATE')")
    @PrivilegeDescription("Privilege to validate timeline yml")
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
    @Operation(summary = "Update timeline properties", description = "Update timeline YAML configuration for tenant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Timeline properties update result"),
        @ApiResponse(responseCode = "500", description = "Internal server error")})
    @SneakyThrows
    @Timed
    @PreAuthorize("hasPermission({'timelineYml': #timelineYml}, 'TIMELINE.TENANT.PROPERTIES.UPDATE')")
    @PrivilegeDescription("Privilege to update timeline yml")
    public ResponseEntity<Void> updateTimelineProperties(@RequestBody String timelineYml) {
        tenantPropertiesService.updateTenantProps(timelineYml);
        return ResponseEntity.ok().build();
    }

}
