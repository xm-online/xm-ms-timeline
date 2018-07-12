package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.service.timeline.TimelineServiceLoggerImpl;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

public class TimelineServiceLoggerUnitTest {

    private TimelineServiceLoggerImpl timelineService;
    private XmTimeline timeline;

    private static final String MS_NAME = "test_ms_name";
    private static final String USER_KEY = "test_user_key";
    private static final Instant DATE = Instant.now();
    private static final String OPERATION = "test_operation";
    private static final Long ENTITY_ID_LONG = 111L;
    private static final String ENTITY_KEY = "test_entity_key";

    @Before
    public void init() {
        timelineService = new TimelineServiceLoggerImpl();
        timeline = createTestTimeline();
    }

    @Test
    public void testLoggerTimelineService() {
        timelineService.insertTimelines(timeline);
        Assertions.assertThat(timelineService.getTimelines(MS_NAME, USER_KEY, ENTITY_ID_LONG.toString(), DATE, DATE, OPERATION, null, 0).getTimelines()).hasSize(1);
        Assertions.assertThat(timelineService.getTimelines(null, null, null, null, null, null, null, 0).getTimelines()).hasSize(1);
        Assertions.assertThat(timelineService.getTimelines("WRONG_MS_NAME", USER_KEY, ENTITY_ID_LONG.toString(), DATE, DATE, OPERATION, null, 0).getTimelines()).hasSize(0);
    }

    private XmTimeline createTestTimeline() {
        XmTimeline timeline = new XmTimeline();
        timeline.setMsName(MS_NAME);
        timeline.setRid("id");
        timeline.setUserKey(USER_KEY);
        timeline.setEntityId(ENTITY_ID_LONG);
        timeline.setEntityKey(ENTITY_KEY);
        timeline.setStartDate(DATE);
        timeline.setOperationName(OPERATION);

        return timeline;
    }
}
