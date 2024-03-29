package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.commons.migration.db.liquibase.LiquibaseRunner;
import com.icthh.xm.commons.migration.db.tenant.DropSchemaResolver;
import com.icthh.xm.ms.timeline.TimelineApp;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.domain.properties.TenantProperties;
import com.icthh.xm.ms.timeline.repository.jpa.TimelineJpaRepository;
import com.icthh.xm.ms.timeline.service.dto.TimelineEvent;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import tech.jhipster.config.JHipsterConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"application.timeline-service-impl = rdbms"})
@SpringBootTest(classes = {TimelineApp.class, DropSchemaResolver.class})
@ActiveProfiles(JHipsterConstants.SPRING_PROFILE_TEST)
public class TimelineServiceIntTest {

    @Autowired
    private TimelineService timelineService;

    @Autowired
    private TimelineJpaRepository timelineJpaRepository;

    @MockBean
    private TenantPropertiesService tenantPropertiesService;

    @MockBean
    private LiquibaseRunner liquibaseRunner;

    private static final Long ID = 1L;
    private static final String MS_NAME = "test_ms_name";
    private static final String USER_KEY = "test_user_key";
    private static final Instant DATE = Instant.now();
    private static final String OPERATION = "test_operation";
    private static final String SOURCE = "test_source";
    private static final String AGGREGATE_ID = "111";
    private static final String AGGREGATE_TYPE = "test_type";
    private static final String ENTITY_KEY = "test_entity_key";
    private static final String TEST_PAYLOAD = "test payload body";

    @Test
    public void testTimelineH2db() {
        mockHidePayloadProp(false);
        timelineJpaRepository.save(createTestTimeline(DATE));

        TimelinePageVM pageVM = timelineService.getTimelines(
            MS_NAME,
            USER_KEY,
            AGGREGATE_ID,
            AGGREGATE_TYPE,
            DATE.minus(1,
                ChronoUnit.DAYS),
            DATE.plus(1,
                ChronoUnit.DAYS),
            OPERATION,
            SOURCE,
            null,
            20,
            Sort.by(Sort.Direction.DESC, "startDate"));
        assertThat(pageVM.getTimelines()).hasSize(1);
        TimelineEvent timelineEvent = pageVM.getTimelines().iterator().next();
        assertThat(timelineEvent.responseBody()).isNotNull();
        assertThat(timelineEvent.requestBody()).isNotNull();

        assertThat(timelineService.getTimelines(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            20,
                Sort.by(Sort.Direction.DESC, "startDate"))
            .getTimelines()).hasSize(1);

        assertThat(timelineService.getTimelines(
            "WRONG_MS_NAME",
            USER_KEY,
            AGGREGATE_ID,
            AGGREGATE_TYPE,
            DATE,
            DATE,
            OPERATION,
            SOURCE,
            null,
            20,
                Sort.by(Sort.Direction.DESC, "startDate"))
            .getTimelines()).isEmpty();

        timelineJpaRepository.deleteAll();
    }

    @Test
    public void testTimelineH2dbWithHidePayload() {
        mockHidePayloadProp(true);
        timelineJpaRepository.save(createTestTimeline(DATE));

        TimelinePageVM pageVM = timelineService.getTimelines(
            MS_NAME,
            USER_KEY,
            AGGREGATE_ID,
            AGGREGATE_TYPE,
            DATE.minus(1,
                ChronoUnit.DAYS),
            DATE.plus(1,
                ChronoUnit.DAYS),
            OPERATION,
            SOURCE,
            null,
            20,
            Sort.by(Sort.Direction.DESC, "startDate"));
        assertThat(pageVM.getTimelines()).hasSize(1);
        TimelineEvent timelineEvent = pageVM.getTimelines().iterator().next();
        assertThat(timelineEvent.responseBody()).isNull();
        assertThat(timelineEvent.requestBody()).isNull();

        timelineJpaRepository.deleteAll();
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testTimelineH2dbExpectedConstraintException() {
        mockHidePayloadProp(false);

        timelineJpaRepository.save(createTestTimeline(null));
    }

    private XmTimeline createTestTimeline(Instant startDate) {
        XmTimeline timeline = new XmTimeline();

        timeline.setId(ID);
        timeline.setMsName(MS_NAME);
        timeline.setUserKey(USER_KEY);
        timeline.setAggregateId(AGGREGATE_ID);
        timeline.setAggregateType(AGGREGATE_TYPE);
        timeline.setEntityKey(ENTITY_KEY);
        timeline.setStartDate(startDate);
        timeline.setOperationName(OPERATION);
        timeline.setSource(SOURCE);
        timeline.setRequestBody(TEST_PAYLOAD);
        timeline.setResponseBody(TEST_PAYLOAD);

        Map<String, String> mapRequestHeaders = new HashMap<>();
        mapRequestHeaders.put("request_header_key_1", "request_header_value_1");
        mapRequestHeaders.put("request_header_key_2", "request_header_value_2");
        timeline.setRequestHeaders(mapRequestHeaders);

        Map<String, String> mapResponseHeaders = new HashMap<>();
        mapResponseHeaders.put("response_header_key_1", "response_header_value_1");
        mapResponseHeaders.put("response_header_key_2", "response_header_value_2");
        timeline.setResponseHeaders(mapResponseHeaders);

        return timeline;
    }

    private void mockHidePayloadProp(Boolean hide) {
        TenantProperties tenantProperties = new TenantProperties();
        TenantProperties.Event event = new TenantProperties.Event();
        event.setHidePayload(hide);
        tenantProperties.setEvent(event);
        when(tenantPropertiesService.getTenantProps()).thenReturn(tenantProperties);
    }
}
