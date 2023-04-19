package com.icthh.xm.ms.timeline.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.icthh.xm.commons.permission.annotation.PrivilegeDescription;
import com.icthh.xm.ms.timeline.service.TimelineService;
import com.icthh.xm.ms.timeline.service.dto.TimelineDto;
import com.icthh.xm.ms.timeline.web.rest.util.PaginationUtil;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

/**
 * REST controller for managing Timelines.
 */
@Api(value = "timelines")
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
    @ApiOperation(value = "Get list of timelines", response = TimelinePageVM.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful retrieval of timelines", response = TimelinePageVM.class),
        @ApiResponse(code = 500, message = "Internal server error")})
    @PreAuthorize("hasPermission({'userKey':#userKey, 'idOrKey': #idOrKey, 'dateFrom': #dateFrom, 'dateTo': #dateTo, "
        + "'operation': #operation}, 'TIMELINE.GET_LIST')")
    @PrivilegeDescription("Privilege to get all the timelines")
    public ResponseEntity<TimelinePageVM> getTimelines(
        @ApiParam(name = "msName", value = "Microservices name for timeline filter")
        @RequestParam(value = "msName", required = false) String msName,
        @ApiParam(name = "userKey", value = "User key for timeline filter")
        @RequestParam(value = "userKey", required = false) String userKey,
        @ApiParam(name = "idOrKey", value = "Entity Id or entity key for timeline filter")
        @RequestParam(value = "idOrKey", required = false) String idOrKey,
        @ApiParam(name = "typeKey", value = "Entity type key for timeline filter")
        @RequestParam(value = "typeKey", required = false) String typeKey,
        @ApiParam(name = "dateFrom", value = "Date from for timeline filter")
        @RequestParam(value = "dateFrom", required = false) Instant dateFrom,
        @ApiParam(name = "dateTo", value = "Date to for timeline filter")
        @RequestParam(value = "dateTo", required = false) Instant dateTo,
        @ApiParam(name = "operation", value = "Operation name for timeline filter")
        @RequestParam(value = "operation", required = false) String operation,
        @ApiParam(name = "source", value = "Source for timeline filter")
        @RequestParam(value = "source", required = false) String source,
        @ApiParam(name = "next", value = "Next value for definition next page")
        @RequestParam(value = "next", required = false) String next,
        @ApiParam(name = "sort", value = "Sorting declared fields")
        @SortDefault(sort = "startDate", direction = Sort.Direction.DESC) Sort sort,
        @ApiParam(name = "limit", value = "Limit of timelines on page", required = true)
        @RequestParam(value = "limit") int limit
    ) {

        return new ResponseEntity<>(
            service.getTimelines(msName, userKey, idOrKey, typeKey, dateFrom, dateTo, operation, source, next, limit, sort), HttpStatus.OK);
    }

    @GetMapping("/timelines/v2")
    @Timed
    @ApiOperation(value = "Get list of timelines (version 2)", response = TimelineDto.class, responseContainer = "List")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful retrieval of timelines", response = TimelineDto.class, responseContainer = "List"),
        @ApiResponse(code = 500, message = "Internal server error")})
    @PreAuthorize("hasPermission({'userKey':#userKey, 'aggregateId': #aggregateId, 'dateFrom': #dateFrom, 'dateTo': #dateTo, "
        + "'operation': #operation}, 'TIMELINE.GET_LIST_V2')")
    @PrivilegeDescription("Privilege to get all the timelines (version 2)")
    public ResponseEntity<List<TimelineDto>> getTimelinesV2(
        @ApiParam(name = "msName", value = "Microservices name for timeline filter")
        @RequestParam(value = "msName", required = false) String msName,
        @ApiParam(name = "userKey", value = "User key for timeline filter")
        @RequestParam(value = "userKey", required = false) String userKey,
        @ApiParam(name = "aggregateId", value = "Entity id for timeline filter")
        @RequestParam(value = "aggregateId", required = false) String aggregateId,
        @ApiParam(name = "aggregateType", value = "Entity type for timeline filter")
        @RequestParam(value = "aggregateType", required = false) String aggregateType,
        @ApiParam(name = "dateFrom", value = "Date from for timeline filter")
        @RequestParam(value = "dateFrom", required = false) Instant dateFrom,
        @ApiParam(name = "dateTo", value = "Date to for timeline filter")
        @RequestParam(value = "dateTo", required = false) Instant dateTo,
        @ApiParam(name = "operation", value = "Operation name for timeline filter")
        @RequestParam(value = "operation", required = false) String operation,
        @ApiParam(name = "source", value = "Source for timeline filter")
        @RequestParam(value = "source", required = false) String source,
        @ApiParam(value = "Results page you want to retrieve (0..N)", defaultValue = "0")
        @RequestParam(value = "page", defaultValue = "0", required = false) int page,
        @ApiParam(value = "Number of records per page", defaultValue = "20")
        @RequestParam(value = "size", required = false, defaultValue = "20") int size,
        @ApiParam(name = "sort", value = "Sorting declared fields")
        @SortDefault(sort = "startDate", direction = Sort.Direction.DESC) Sort sort
    ) {
        Page<TimelineDto> timelines = service.getTimelines(msName, userKey, aggregateId, aggregateType, dateFrom, dateTo, operation, source, page, size, sort);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(timelines, "/timelines/v2");
        return new ResponseEntity<>(timelines.getContent(), headers, HttpStatus.OK);
    }

}
