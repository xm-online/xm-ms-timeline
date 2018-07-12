package com.icthh.xm.ms.timeline.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.icthh.xm.ms.timeline.service.timeline.TimelineService;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

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
     * @param next the next value for definition next page in cassandra
     * @param limit the limit of timelines on page
     * @return the ResponseEntity with status 200 (OK) and the list of xmTimelines and next page code in body.
     */
    @GetMapping("/timelines")
    @Timed
    @ApiOperation(value = "Get list of timelines", response = TimelinePageVM.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful retrieval of timelines", response = TimelinePageVM.class),
        @ApiResponse(code = 500, message = "Internal server error")})
    @PreAuthorize("hasPermission({'userKey':#userKey, 'idOrKey': #idOrKey, 'dateFrom': #dateFrom, 'dateTo': #dateTo, "
        + "'operation': #operation}, 'TIMELINE.GET_LIST')")
    public ResponseEntity<TimelinePageVM> getTimelines(
        @ApiParam(name = "msName", value = "Microservices name for timeline filter")
        @RequestParam(value = "msName", required = false) String msName,
        @ApiParam(name = "userKey", value = "User key for timeline filter")
        @RequestParam(value = "userKey", required = false) String userKey,
        @ApiParam(name = "idOrKey", value = "Entity Id or entity key for timeline filter")
        @RequestParam(value = "idOrKey", required = false) String idOrKey,
        @ApiParam(name = "dateFrom", value = "Date from for timeline filter")
        @RequestParam(value = "dateFrom", required = false) Instant dateFrom,
        @ApiParam(name = "dateTo", value = "Date to for timeline filter")
        @RequestParam(value = "dateTo", required = false) Instant dateTo,
        @ApiParam(name = "operation", value = "Operation name for timeline filter")
        @RequestParam(value = "operation", required = false) String operation,
        @ApiParam(name = "next", value = "Next value for definition next page in cassandra")
        @RequestParam(value = "next", required = false) String next,
        @ApiParam(name = "limit", value = "Limit of timelines on page", required = true)
        @RequestParam(value = "limit") int limit
    ) {

        return new ResponseEntity<>(
            service.getTimelines(msName, userKey, idOrKey, dateFrom, dateTo, operation, next, limit), HttpStatus.OK);
    }

}
