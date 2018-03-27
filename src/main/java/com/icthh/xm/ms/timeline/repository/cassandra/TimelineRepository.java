package com.icthh.xm.ms.timeline.repository.cassandra;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.gte;
import static com.datastax.driver.core.querybuilder.QueryBuilder.lte;
import static java.lang.Boolean.TRUE;

import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.repository.cassandra.mapper.TimelineMapper;
import com.icthh.xm.ms.timeline.service.TenantPropertiesService;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TimelineRepository {

    private static final String TABLE_TIMELINE_BY_USER_AND_DATE = "timeline_by_user_and_date";
    private static final String TABLE_TIMELINE_BY_USER_AND_OP_AND_DATE = "timeline_by_user_and_operation_and_date";

    private static final String TABLE_TIMELINE_BY_ENTITY_AND_DATE = "timeline_by_entity_and_date";
    private static final String TABLE_TIMELINE_BY_ENTITY_AND_OP_AND_DATE = "timeline_by_entity_and_operation_and_date";

    private static final String LOGIN_COL = "login";
    private static final String USER_KEY_COL = "user_key";
    private static final String OPERATION_COL = "operation";
    private static final String START_DATE_COL = "start_date";
    private static final String ENTITY_ID_COL = "entity_id";
    private static final String RID_COL = "rid";
    private static final String TENANT_COL = "tenant";
    private static final String MS_NAME_COL = "ms_name";
    private static final String ENTITY_KEY_COL = "entity_key";
    private static final String ENTITY_TYPE_KEY_COL = "entity_type_key";
    private static final String OPERATION_URL_COL = "operation_url";
    private static final String HTTP_METHOD_COL = "http_method";
    private static final String REQUEST_BODY_COL = "request_body";
    private static final String REQUEST_LENGTH_COL = "request_length";
    private static final String RESPONSE_BODY_COL = "response_body";
    private static final String RESPONSE_LENGTH_COL = "response_length";
    private static final String REQUEST_HEADERS_COL = "request_headers";
    private static final String RESPONSE_HEADERS_COL = "response_headers";
    private static final String HTTP_STATUS_CODE_COL = "http_status_code";
    private static final String CHANNEL_TYPE_COL = "channel_type";
    private static final String EXEC_TIME_COL = "exec_time";

    private static final Set<String> ALL_FIELDS = new HashSet<>();

    static {
        Collections.addAll(ALL_FIELDS, LOGIN_COL, USER_KEY_COL, OPERATION_COL, START_DATE_COL, ENTITY_ID_COL,
            RID_COL, TENANT_COL, MS_NAME_COL, ENTITY_KEY_COL, ENTITY_TYPE_KEY_COL, OPERATION_URL_COL,
            HTTP_METHOD_COL, REQUEST_BODY_COL, REQUEST_LENGTH_COL, RESPONSE_BODY_COL, RESPONSE_LENGTH_COL,
            REQUEST_HEADERS_COL, RESPONSE_HEADERS_COL, HTTP_STATUS_CODE_COL, CHANNEL_TYPE_COL, EXEC_TIME_COL);
    }

    private TenantPropertiesService tenantPropertiesService;
    private TenantContextHolder tenantContextHolder;
    private Session session;

    public TimelineRepository(TenantPropertiesService tenantPropertiesService,
                              TenantContextHolder tenantContextHolder,
                              Session session) {
        this.tenantPropertiesService = tenantPropertiesService;
        this.tenantContextHolder = tenantContextHolder;
        this.session = session;
    }

    /**
     * Get timelines by user key and date.
     *
     * @param userKey  the user key
     * @param dateFrom the date from
     * @param dateTo   the date to
     * @param page     the next page code
     * @param limit    the limit per page
     * @return timeline page with list of timelines and next page code
     */
    public TimelinePageVM getTimelinesByUserKeyAndDate(String userKey,
                                                       Instant dateFrom,
                                                       Instant dateTo,
                                                       String page,
                                                       int limit,
                                                       String msName) {
        Select select = QueryBuilder.select(getFields()).from(
                        TenantContextUtils.getRequiredTenantKeyValue(tenantContextHolder),
                        TABLE_TIMELINE_BY_USER_AND_DATE);
        select.where(eq(USER_KEY_COL, userKey));
        setFilterByMsNameIfPassed(select, msName);
        prepareWhereClause(select, null, dateFrom, dateTo, limit);
        return getPage(select, page, limit);
    }

    /**
     * Get timelines by user key, operation and date.
     *
     * @param userKey   the user key
     * @param operation the operation
     * @param dateFrom  the date from
     * @param dateTo    the date to
     * @param page      the next page code
     * @param limit     the limit per page
     * @return timeline page with list of timelines and next page code
     */
    public TimelinePageVM getTimelinesByUserKeyAndOpAndDate(String userKey,
                                                            String operation,
                                                            Instant dateFrom,
                                                            Instant dateTo,
                                                            String page,
                                                            int limit,
                                                            String msName) {
        Select select = QueryBuilder.select(getFields()).from(
                        TenantContextUtils.getRequiredTenantKeyValue(tenantContextHolder),
                        TABLE_TIMELINE_BY_USER_AND_OP_AND_DATE);
        select.where(eq(USER_KEY_COL, userKey));
        setFilterByMsNameIfPassed(select, msName);
        prepareWhereClause(select, operation, dateFrom, dateTo, limit);
        return getPage(select, page, limit);
    }

    /**
     * Get timelines by entity id and date.
     *
     * @param id       the entity id
     * @param dateFrom the date from
     * @param dateTo   the date to
     * @param page     the next page code
     * @param limit    the limit per page
     * @return timeline page with list of timelines and next page code
     */
    public TimelinePageVM getTimelinesByEntityAndDate(Long id,
                                                      Instant dateFrom,
                                                      Instant dateTo,
                                                      String page,
                                                      int limit,
                                                      String msName) {
        Select select = QueryBuilder.select(getFields()).from(
                        TenantContextUtils.getRequiredTenantKeyValue(tenantContextHolder),
                        TABLE_TIMELINE_BY_ENTITY_AND_DATE);
        select.where(eq(ENTITY_ID_COL, id));
        setFilterByMsNameIfPassed(select, msName);
        prepareWhereClause(select, null, dateFrom, dateTo, limit);
        return getPage(select, page, limit);
    }

    /**
     * Get timelines by entity id, operation and date.
     *
     * @param id        the entity id
     * @param operation the operation
     * @param dateFrom  the date from
     * @param dateTo    the date to
     * @param page      the next page code
     * @param limit     the limit per page
     * @return timeline page with list of timelines and next page code
     */
    public TimelinePageVM getTimelinesByEntityAndOpAndDate(Long id,
                                                           String operation,
                                                           Instant dateFrom,
                                                           Instant dateTo,
                                                           String page,
                                                           int limit,
                                                           String msName) {
        Select select = QueryBuilder.select(getFields()).from(
                        TenantContextUtils.getRequiredTenantKeyValue(tenantContextHolder),
                        TABLE_TIMELINE_BY_ENTITY_AND_OP_AND_DATE);
        select.where(eq(ENTITY_ID_COL, id));
        setFilterByMsNameIfPassed(select, msName);
        prepareWhereClause(select, operation, dateFrom, dateTo, limit);
        return getPage(select, page, limit);
    }

    private void prepareWhereClause(Select select, String operation, Instant dateFrom, Instant dateTo, int limit) {
        if (StringUtils.isNotBlank(operation)) {
            select.where(eq(OPERATION_COL, operation));
        }
        if (dateFrom != null) {
            select.where(gte(START_DATE_COL, Date.from(dateFrom)));
        }
        if (dateTo != null) {
            select.where(lte(START_DATE_COL, Date.from(dateTo)));
        }

        select.setFetchSize(limit);
    }

    /**
     * Insert timeline into timeline_by_user_and_date.
     *
     * @param xmTimeline the timeline
     * @return the result of insert
     */
    public boolean insertTimelineByUserAndDate(XmTimeline xmTimeline) {
        return insertTimeline(xmTimeline, TABLE_TIMELINE_BY_USER_AND_DATE);
    }

    /**
     * Insert timeline into timeline_by_user_and_operation_and_date.
     *
     * @param xmTimeline the timeline
     * @return the result of insert
     */
    public boolean insertTimelineByUserAndOperationAndDate(XmTimeline xmTimeline) {
        return insertTimeline(xmTimeline, TABLE_TIMELINE_BY_USER_AND_OP_AND_DATE);
    }

    /**
     * Insert timeline into timeline_by_entity_and_date.
     *
     * @param xmTimeline the timeline
     * @return the result of insert
     */
    public boolean insertTimelineByEntityAndDate(XmTimeline xmTimeline) {
        return insertTimeline(xmTimeline, TABLE_TIMELINE_BY_ENTITY_AND_DATE);
    }

    /**
     * Insert timeline into timeline_by_entity_and_operation_and_date.
     *
     * @param xmTimeline the timeline
     * @return the result of insert
     */
    public boolean insertTimelineByEntityAndOperationAndDate(XmTimeline xmTimeline) {
        return insertTimeline(xmTimeline, TABLE_TIMELINE_BY_ENTITY_AND_OP_AND_DATE);
    }

    private TimelinePageVM getPage(Select select, String page, int limit) {
        //If we have a 'next' page set we deserialise it and add it to the select
        //statement
        if (page != null) {
            select.setPagingState(PagingState.fromString(page));
        }

        //Execute the query
        ResultSet resultSet = session.execute(select);

        //Get the next paging state
        PagingState newPagingState = resultSet.getExecutionInfo().getPagingState();
        //The number of rows that can be read without fetching
        int remaining = resultSet.getAvailableWithoutFetching();

        List<XmTimeline> timelines = new ArrayList<>(limit);

        for (Row row : resultSet) {
            XmTimeline timeline = TimelineMapper.createTimeline(row);

            timelines.add(timeline);

            //If we can't move to the next row without fetching we break
            if (--remaining == 0) {
                break;
            }
        }

        //Serialise the next paging state
        String serializedNewPagingState = newPagingState != null
            ? newPagingState.toString() :
            null;

        //Return an object with a list of timelines and the next paging state
        return new TimelinePageVM(timelines, serializedNewPagingState);
    }

    private boolean insertTimeline(XmTimeline xmTimeline, String table) {
        Insert insert = QueryBuilder.insertInto(xmTimeline.getTenant(), table)
            .value(LOGIN_COL, xmTimeline.getLogin())
            .value(USER_KEY_COL, xmTimeline.getUserKey())
            .value(START_DATE_COL, xmTimeline.getStartDate())
            .value(RID_COL, xmTimeline.getRid())
            .value(TENANT_COL, xmTimeline.getTenant())
            .value(MS_NAME_COL, xmTimeline.getMsName())
            .value(OPERATION_COL, xmTimeline.getOperationName())
            .value(ENTITY_ID_COL, xmTimeline.getEntityId())
            .value(ENTITY_KEY_COL, xmTimeline.getEntityKey())
            .value(ENTITY_TYPE_KEY_COL, xmTimeline.getEntityTypeKey())
            .value(OPERATION_URL_COL, xmTimeline.getOperationUrl())
            .value(HTTP_METHOD_COL, xmTimeline.getHttpMethod())
            .value(REQUEST_BODY_COL, xmTimeline.getRequestBody())
            .value(REQUEST_LENGTH_COL, xmTimeline.getRequestLength())
            .value(RESPONSE_BODY_COL, xmTimeline.getResponseBody())
            .value(RESPONSE_LENGTH_COL, xmTimeline.getResponseLength())
            .value(REQUEST_HEADERS_COL, xmTimeline.getRequestHeaders())
            .value(RESPONSE_HEADERS_COL, xmTimeline.getResponseHeaders())
            .value(HTTP_STATUS_CODE_COL, xmTimeline.getHttpStatusCode())
            .value(CHANNEL_TYPE_COL, xmTimeline.getChannelType())
            .value(EXEC_TIME_COL, xmTimeline.getExecTime());

        ResultSet resultSet = session.execute(insert);
        return resultSet.wasApplied();
    }

    private String[] getFields() {
        return ALL_FIELDS.stream()
            .filter(this::payloadCondition)
            .toArray(String[]::new);
    }

    /**
     * Hide request and response body if hide-payload parameter set to true.
     * @param column the column to filter
     * @return true if column passes payload condition
     */
    private boolean payloadCondition(String column) {
        return !(TRUE.equals(tenantPropertiesService.getTenantProps().getEvent().getHidePayload())
            && (REQUEST_BODY_COL.equals(column) || RESPONSE_BODY_COL.equals(column)));
    }

    /**
     * Sets microservice name to 'where' clause if it passed.
     * @param select select request builder object
     * @param msName microservice name
     */
    public void setFilterByMsNameIfPassed(Select select, String msName) {
        if (StringUtils.isNoneBlank(msName)) {
            select.where(eq(MS_NAME_COL, msName));
        }
    }
}
