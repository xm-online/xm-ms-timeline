package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.ms.timeline.domain.XmTimeline;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Sort;

import java.time.Instant;

import static org.springframework.data.domain.Sort.Direction.DESC;

public class SortProcessorUnitTest {

    private static final Long ID = 1L;
    private static final String RID = "test_rid";
    private static final String LOGIN = "test_login";
    private static final String USER_KEY = "test_user_key";
    private static final String MS_NAME = "test_ms_name";
    private static final String OPERATION_NAME = "test_operation";
    private static final String AGGREGATE_ID = "111";
    private static final String ENTITY_KEY = "test_entity_key";
    private static final String AGGREGATE_TYPE = "test_aggregate_type";
    private static final Instant DATE = Instant.now();
    private static final String CLIENT_ID = "test_client_id";

    private static final String ID_FIELD = "id";
    private static final String RID_FIELD = "rid";
    private static final String LOGIN_FIELD = "login";
    private static final String USER_KEY_FIELD = "userKey";
    private static final String MS_NAME_FIELD = "msName";
    private static final String OPERATION_NAME_FIELD = "operationName";
    private static final String AGGREGATE_ID_FIELD = "aggregateId";
    private static final String ENTITY_KEY_FIELD = "entityKey";
    private static final String AGGREGATE_TYPE_FIELD = "aggregateType";
    private static final String START_DATE_FIELD = "startDate";
    private static final String CLIENT_ID_FIELD = "clientId";

    private static final String FAKE_FIRST_FIELD = "fieldFirst";
    private static final String FAKE_SECOND_FIELD = "fieldSecond";

    private final XmTimeline xmTimeline = createTestTimeline();
    private final Sort defaultSort = getDefaultSort();
    private final Sort validSort = getCustomValidSort();
    private final Sort notValidSort = getCustomNotValidSort();

    private SortProcessor sortProcessor;

    @Before
    public void init() {
        sortProcessor = new SortProcessor();
    }

    @Test
    public void findValidOrDefault_shouldReturnDefaultSort_sortParametersAreNotValid() {

        Sort actual = sortProcessor.findValidOrDefault(xmTimeline.getClass(), notValidSort, defaultSort);

        Assert.assertEquals(defaultSort, actual);
    }

    @Test
    public void findValidOrDefault_shouldCustomSort_sortParametersAreValid() {

        Sort actual = sortProcessor.findValidOrDefault(xmTimeline.getClass(), validSort, defaultSort);

        Assert.assertEquals(validSort, actual);
    }

    private XmTimeline createTestTimeline() {
        XmTimeline timeline = new XmTimeline();

        timeline.setId(ID);
        timeline.setRid(RID);
        timeline.setLogin(LOGIN);
        timeline.setUserKey(USER_KEY);
        timeline.setMsName(MS_NAME);
        timeline.setOperationName(OPERATION_NAME);
        timeline.setAggregateId(AGGREGATE_ID);
        timeline.setEntityKey(ENTITY_KEY);
        timeline.setAggregateType(AGGREGATE_TYPE);
        timeline.setStartDate(DATE);
        timeline.setClientId(CLIENT_ID);

        return timeline;
    }

    private Sort getDefaultSort() {
        return Sort.by(DESC, START_DATE_FIELD);
    }

    private Sort getCustomNotValidSort() {
        return Sort.by(
            FAKE_FIRST_FIELD,
            FAKE_SECOND_FIELD
        );
    }

    private Sort getCustomValidSort() {
        return Sort.by(
            ID_FIELD,
            RID_FIELD,
            LOGIN_FIELD,
            USER_KEY_FIELD,
            MS_NAME_FIELD,
            OPERATION_NAME_FIELD,
            AGGREGATE_ID_FIELD,
            ENTITY_KEY_FIELD,
            AGGREGATE_TYPE_FIELD,
            START_DATE_FIELD,
            CLIENT_ID_FIELD
        );
    }

}
