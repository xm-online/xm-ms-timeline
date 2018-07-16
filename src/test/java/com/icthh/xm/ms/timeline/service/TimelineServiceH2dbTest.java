package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.ms.timeline.AbstractCassandraTest;
import com.icthh.xm.ms.timeline.TimelineApp;
import com.icthh.xm.ms.timeline.config.SecurityBeanOverrideConfiguration;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.service.timeline.TimelineService;
import com.icthh.xm.ms.timeline.service.timeline.TimelineServiceH2dbImpl;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;
import io.github.jhipster.config.JHipsterConstants;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TimelineApp.class, SecurityBeanOverrideConfiguration.class})
@TestPropertySource(properties = { "application.timeline-service-impl = h2db"})
@ActiveProfiles(JHipsterConstants.SPRING_PROFILE_TEST)
public class TimelineServiceH2dbTest {

    @Autowired
    private TimelineService timelineService;

    private static final Long ID = 1L;
    private static final String MS_NAME = "test_ms_name";
    private static final String USER_KEY = "test_user_key";
    private static final Instant DATE = Instant.now();
    private static final String OPERATION = "test_operation";
    private static final Long ENTITY_ID_LONG = 111L;
    private static final String ENTITY_KEY = "test_entity_key";

//    @MockBean
//    private TimelineJpaRepository timelineRepository;

    @Test
    public void testTimelineH2db() {
        timelineService.insertTimelines(createTestTimeline());
        Assertions.assertThat(timelineService.getTimelines(MS_NAME, USER_KEY, ENTITY_ID_LONG.toString(), DATE.minus(1, ChronoUnit.DAYS), DATE.plus(1, ChronoUnit.DAYS), OPERATION, null, 0).getTimelines()).hasSize(1);
        Assertions.assertThat(timelineService.getTimelines(null, null, null, null, null, null, null, 0).getTimelines()).hasSize(1);
        Assertions.assertThat(timelineService.getTimelines("WRONG_MS_NAME", USER_KEY, ENTITY_ID_LONG.toString(), DATE, DATE, OPERATION, null, 0).getTimelines()).hasSize(0);
    }

    private XmTimeline createTestTimeline() {
        XmTimeline timeline = new XmTimeline();

        timeline.setId(ID);
        timeline.setMsName(MS_NAME);
        timeline.setUserKey(USER_KEY);
        timeline.setEntityId(ENTITY_ID_LONG);
        timeline.setEntityKey(ENTITY_KEY);
        timeline.setStartDate(DATE);
        timeline.setOperationName(OPERATION);

        Map mapRequestHeaders = new HashMap();
        mapRequestHeaders.put("request_header_key_1", "request_header_value_1");
        mapRequestHeaders.put("request_header_key_2", "request_header_value_2");
        timeline.setRequestHeaders(mapRequestHeaders);

        Map mapResponseHeaders = new HashMap();
        mapResponseHeaders.put("response_header_key_1", "response_header_value_1");
        mapResponseHeaders.put("response_header_key_2", "response_header_value_2");
        timeline.setResponseHeaders(mapResponseHeaders);

        return timeline;
    }
}
