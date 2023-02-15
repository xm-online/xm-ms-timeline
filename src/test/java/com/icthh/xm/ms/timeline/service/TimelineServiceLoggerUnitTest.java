package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.service.dto.Timeline;
import com.icthh.xm.ms.timeline.service.logger.TimelineServiceLoggerImpl;
import com.icthh.xm.ms.timeline.service.mapper.XmTimelineMapper;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;

import java.time.Instant;

public class TimelineServiceLoggerUnitTest {

    private TimelineServiceLoggerImpl timelineService;
    private XmTimeline timeline;

    private static final String MS_NAME = "test_ms_name";
    private static final String USER_KEY = "test_user_key";
    private static final Instant DATE = Instant.now();
    private static final String OPERATION = "test_operation";
    private static final String SOURCE = "test_source";
    private static final String AGGREGATE_ID = "111";
    private static final String ENTITY_KEY = "test_entity_key";

    @Before
    public void init() {
        timelineService = new TimelineServiceLoggerImpl(new XmTimelineMapper());
        timeline = createTestTimeline();
        timelineService.insertTimelines(timeline);
    }

    @Test
    public void getTimelinesList() {
        Assertions.assertThat(timelineService.getTimelines(
            MS_NAME,
            USER_KEY,
            AGGREGATE_ID,
            DATE,
            DATE,
            OPERATION,
            SOURCE,
            null,
            0,
                null)
            .getTimelines()).hasSize(1);

        Assertions.assertThat(timelineService.getTimelines(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0,
                null)
            .getTimelines()).hasSize(1);

        Assertions.assertThat(timelineService.getTimelines(
            "WRONG_MS_NAME",
            USER_KEY,
            AGGREGATE_ID,
            DATE,
            DATE,
            OPERATION,
            SOURCE,
            null,
            0,
                null)
            .getTimelines()).hasSize(0);
    }

    @Test
    public void getTimelinesPage() {
        Page<Timeline> page = timelineService.getTimelines(
                MS_NAME,
                USER_KEY,
                AGGREGATE_ID,
                DATE,
                DATE,
                OPERATION,
                SOURCE,
                0,
                0,
                null);
        Assertions.assertThat(page.getContent()).hasSize(1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);

        page = timelineService.getTimelines(
            "WRONG_MS_NAME",
            USER_KEY,
            AGGREGATE_ID,
            DATE,
            DATE,
            OPERATION,
            SOURCE,
            0,
            0,
            null);
        Assertions.assertThat(page.getContent()).hasSize(0);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(0);
    }

    private XmTimeline createTestTimeline() {
        XmTimeline timeline = new XmTimeline();
        timeline.setMsName(MS_NAME);
        timeline.setRid("id");
        timeline.setUserKey(USER_KEY);
        timeline.setAggregateId(AGGREGATE_ID);
        timeline.setEntityKey(ENTITY_KEY);
        timeline.setStartDate(DATE);
        timeline.setOperationName(OPERATION);
        timeline.setSource(SOURCE);

        return timeline;
    }
}
