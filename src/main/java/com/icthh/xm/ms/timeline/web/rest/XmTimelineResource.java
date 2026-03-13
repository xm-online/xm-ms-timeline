package com.icthh.xm.ms.timeline.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.icthh.xm.commons.permission.annotation.PrivilegeDescription;
import com.icthh.xm.ms.timeline.service.TimelineService;
import com.icthh.xm.ms.timeline.service.dto.TimelineDto;
import com.icthh.xm.ms.timeline.template.TemplateParamsHolder;
import com.icthh.xm.ms.timeline.web.rest.util.PaginationUtil;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

/**
 * REST controller for managing Timelines.
 */
@Tag(name = "timelines", description = "Timeline API")
@RestController
@RequestMapping("/api")
@Slf4j
public class XmTimelineResource {

    private TimelineService service;

    public XmTimelineResource(TimelineService service) {
        this.service = service;
    }

    /**
     * GET  /timelines : get all the timelines.
     *
     * @param userKey the user key for timeline filter
     * @param idOrKey the entity id or entity key for timeline filter
     * @param dateFrom the date from for timeline filter
     * @param dateTo the date to for timeline filter
     * @param next the next value for definition next page
     * @param sort sorting the list of xmTimelines
     * @param limit the limit of timelines on page
     * @return the ResponseEntity with status 200 (OK) and the list of xmTimelines and next page code in body.
     */
    @GetMapping("/timelines")
    @Timed
    @Deprecated
    @Operation(summary = "Get list of timelines", description = "Retrieve paginated list of timeline events (deprecated, use v2)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of timelines",
            content = @Content(schema = @Schema(implementation = TimelinePageVM.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PreAuthorize("hasPermission({'userKey':#userKey, 'idOrKey': #idOrKey, 'dateFrom': #dateFrom, 'dateTo': #dateTo, "
        + "'operation': #operation}, 'TIMELINE.GET_LIST')")
    @PrivilegeDescription("Privilege to get all the timelines")
    public ResponseEntity<TimelinePageVM> getTimelines(
        @Parameter(name = "msName", description = "Microservices name for timeline filter")
        @RequestParam(value = "msName", required = false) String msName,
        @Parameter(name = "userKey", description = "User key for timeline filter")
        @RequestParam(value = "userKey", required = false) String userKey,
        @Parameter(name = "idOrKey", description = "Entity Id or entity key for timeline filter")
        @RequestParam(value = "idOrKey", required = false) String idOrKey,
        @Parameter(name = "typeKey", description = "Entity type key for timeline filter")
        @RequestParam(value = "typeKey", required = false) String typeKey,
        @Parameter(name = "dateFrom", description = "Date from for timeline filter")
        @RequestParam(value = "dateFrom", required = false) Instant dateFrom,
        @Parameter(name = "dateTo", description = "Date to for timeline filter")
        @RequestParam(value = "dateTo", required = false) Instant dateTo,
        @Parameter(name = "operation", description = "Operation name for timeline filter")
        @RequestParam(value = "operation", required = false) String operation,
        @Parameter(name = "source", description = "Source for timeline filter")
        @RequestParam(value = "source", required = false) String source,
        @Parameter(name = "next", description = "Next value for definition next page")
        @RequestParam(value = "next", required = false) String next,
        @Parameter(name = "sort", description = "Sorting declared fields")
        @SortDefault(sort = "startDate", direction = Sort.Direction.DESC) Sort sort,
        @Parameter(name = "limit", description = "Limit of timelines on page", required = true)
        @RequestParam(value = "limit") int limit
    ) {

        return new ResponseEntity<>(
            service.getTimelines(msName, userKey, idOrKey, typeKey, dateFrom, dateTo, operation, source, next, limit, sort), HttpStatus.OK);
    }

    @GetMapping("/timelines/v2")
    @Timed
    @Operation(summary = "Get list of timelines (version 2)", description = "Retrieve paginated list of timeline events with advanced filtering including clientId")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of timelines",
            content = @Content(schema = @Schema(implementation = TimelineDto.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PreAuthorize("hasPermission({'userKey':#userKey, 'aggregateId': #aggregateId, 'dateFrom': #dateFrom, 'dateTo': #dateTo, "
        + "'operation': #operation}, 'TIMELINE.GET_LIST_V2')")
    @PrivilegeDescription("Privilege to get all the timelines (version 2)")
    public ResponseEntity<List<TimelineDto>> getTimelinesV2(
        @Parameter(name = "msName", description = "Microservices name for timeline filter", example = "entity")
        @RequestParam(value = "msName", required = false) String msName,
        @Parameter(name = "userKey", description = "User key for timeline filter", example = "user123")
        @RequestParam(value = "userKey", required = false) String userKey,
        @Parameter(name = "aggregateId", description = "Entity id for timeline filter", example = "12345")
        @RequestParam(value = "aggregateId", required = false) String aggregateId,
        @Parameter(name = "aggregateType", description = "Entity type for timeline filter", example = "ACCOUNT")
        @RequestParam(value = "aggregateType", required = false) String aggregateType,
        @Parameter(name = "dateFrom", description = "Date from for timeline filter (ISO-8601)", example = "2026-03-01T00:00:00Z")
        @RequestParam(value = "dateFrom", required = false) Instant dateFrom,
        @Parameter(name = "dateTo", description = "Date to for timeline filter (ISO-8601)", example = "2026-03-12T23:59:59Z")
        @RequestParam(value = "dateTo", required = false) Instant dateTo,
        @Parameter(name = "operation", description = "Operation name for timeline filter", example = "UPDATE")
        @RequestParam(value = "operation", required = false) String operation,
        @Parameter(name = "source", description = "Source for timeline filter", example = "WEB")
        @RequestParam(value = "source", required = false) String source,
        @Parameter(name = "clientId", description = "Client ID for timeline filter", example = "client-456")
        @RequestParam(value = "clientId", required = false) String clientId,
        @Parameter(description = "Results page you want to retrieve (0..N)", example = "0")
        @RequestParam(value = "page", defaultValue = "0", required = false) int page,
        @Parameter(description = "Number of records per page", example = "20")
        @RequestParam(value = "size", required = false, defaultValue = "20") int size,
        @Parameter(name = "sort", description = "Sorting declared fields (e.g., startDate,desc)", example = "startDate,desc")
        @SortDefault(sort = "startDate", direction = Sort.Direction.DESC) Sort sort
    ) {
        Page<TimelineDto> timelines = service.getTimelines(msName, userKey, aggregateId, aggregateType, dateFrom, dateTo, operation, source, clientId, page, size, sort);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(timelines, "/timelines/v2");
        return new ResponseEntity<>(timelines.getContent(), headers, HttpStatus.OK);
    }

    @Timed
    @PreAuthorize("hasPermission({'templateKey': #templateKey}, 'TIMELINE.TEMPLATE')")
    @PostMapping("/search/{templateKey}")
    public ResponseEntity<List<TimelineDto>> searchByTemplate(@PathVariable String templateKey,
                                                              Pageable pageable,
                                                              @RequestParam(required = false) String outFormat,
                                                              @RequestBody TemplateParamsHolder templateParamsHolder) {

        Page<TimelineDto> timelines = service.searchByTemplate(templateKey, templateParamsHolder, pageable, null);
        HttpHeaders headers = PaginationUtil
            .generatePaginationHttpHeaders(timelines, "/api/search/" + templateKey);
        return new ResponseEntity<>(timelines.getContent(), headers, HttpStatus.OK);
    }
}
