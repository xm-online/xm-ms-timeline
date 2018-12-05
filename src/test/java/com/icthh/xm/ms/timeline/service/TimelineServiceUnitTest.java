package com.icthh.xm.ms.timeline.service;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.icthh.xm.commons.tenant.TenantContext;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantKey;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.repository.cassandra.EntityMappingRepository;
import com.icthh.xm.ms.timeline.repository.cassandra.TimelineRepository;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;

import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

public class TimelineServiceUnitTest {

    private static final String MS_NAME = "test";
    private static final String USER_KEY = "test";
    private static final Instant DATE = Instant.now();
    private static final String OPERATION = "testOperation";
    private static final String ENTITY_ID = "111";
    private static final Long ENTITY_ID_LONG = 111L;
    private static final String ENTITY_KEY = "test.key";
    private static final int LIMIT = 10;
    private static final String TENANT = "xm";

    private TimelineRepository timelineRepository;
    private EntityMappingRepository entityMappingRepository;
    private TenantContextHolder tenantContextHolder;
    private TimelineCassandraService timelineCassandraService;
    private TenantContext tenantContext;

    @Before
    public void init() {

        timelineRepository = mock(TimelineRepository.class);
        entityMappingRepository = mock(EntityMappingRepository.class);
        tenantContextHolder = mock(TenantContextHolder.class);
        timelineCassandraService = new TimelineCassandraService(timelineRepository, entityMappingRepository,
                        tenantContextHolder);
        tenantContext = mock(TenantContext.class);
        when(tenantContext.getTenantKey()).thenReturn(Optional.of(TenantKey.valueOf(TENANT)));
        when(tenantContextHolder.getContext()).thenReturn(tenantContext);
    }

    @Test
    public void testGetByUserKey() {
        when(timelineRepository.getTimelinesByUserKeyAndDate(USER_KEY, DATE, DATE, null, LIMIT, null))
            .thenReturn(new TimelinePageVM(new ArrayList<>(), null));
        timelineCassandraService.getTimelines(null, USER_KEY, null, DATE, DATE, null, null, LIMIT);
        verify(timelineRepository).getTimelinesByUserKeyAndDate(USER_KEY, DATE, DATE, null, LIMIT, null);
    }

    @Test
    public void testGetByUserKeyAndMsName() {
        when(timelineRepository.getTimelinesByUserKeyAndDate(USER_KEY, DATE, DATE, null, LIMIT, null))
            .thenReturn(new TimelinePageVM(new ArrayList<>(), null));
        timelineCassandraService.getTimelines(MS_NAME, USER_KEY, null, DATE, DATE, null, null, LIMIT);
        verify(timelineRepository).getTimelinesByUserKeyAndDate(USER_KEY, DATE, DATE, null, LIMIT, MS_NAME);

    }

    @Test
    public void testGetByEntityId() {
        when(timelineRepository.getTimelinesByEntityAndDate(ENTITY_ID_LONG, DATE, DATE, null, LIMIT, null))
            .thenReturn(new TimelinePageVM(new ArrayList<>(), null));
        timelineCassandraService.getTimelines(null, null, ENTITY_ID, DATE, DATE, null, null, LIMIT);
        verify(timelineRepository).getTimelinesByEntityAndDate(ENTITY_ID_LONG, DATE, DATE, null, LIMIT, null);

    }

    @Test
    public void testGetByEntityIdAndOp() {
        when(timelineRepository.getTimelinesByEntityAndOpAndDate(ENTITY_ID_LONG, OPERATION, DATE, DATE, null, LIMIT, null))
            .thenReturn(new TimelinePageVM(new ArrayList<>(), null));
        timelineCassandraService.getTimelines(null, null, ENTITY_ID, DATE, DATE, OPERATION, null, LIMIT);
        verify(timelineRepository).getTimelinesByEntityAndOpAndDate(ENTITY_ID_LONG, OPERATION, DATE, DATE, null, LIMIT, null);
    }

    @Test
    public void testGetByEntityKey() {
        when(entityMappingRepository.getIdByKey(eq(ENTITY_KEY), anyString()))
            .thenReturn(ENTITY_ID_LONG);
        when(timelineRepository.getTimelinesByEntityAndDate(ENTITY_ID_LONG, DATE, DATE, null, LIMIT, null))
            .thenReturn(new TimelinePageVM(new ArrayList<>(), null));
        timelineCassandraService.getTimelines(null, null, ENTITY_KEY, DATE, DATE, null, null, LIMIT);
        verify(timelineRepository).getTimelinesByEntityAndDate(ENTITY_ID_LONG, DATE, DATE, null, LIMIT, null);
        verify(entityMappingRepository).getIdByKey(eq(ENTITY_KEY), anyString());
    }

    @Test
    public void testGetByEntityKeyAndOp() {
        when(entityMappingRepository.getIdByKey(eq(ENTITY_KEY), anyString()))
            .thenReturn(ENTITY_ID_LONG);
        when(timelineRepository.getTimelinesByEntityAndOpAndDate(ENTITY_ID_LONG, OPERATION, DATE, DATE, null, LIMIT, null))
            .thenReturn(new TimelinePageVM(new ArrayList<>(), null));
        timelineCassandraService.getTimelines(null, null, ENTITY_KEY, DATE, DATE, OPERATION, null, LIMIT);
        verify(timelineRepository).getTimelinesByEntityAndOpAndDate(ENTITY_ID_LONG, OPERATION, DATE, DATE, null, LIMIT, null);
        verify(entityMappingRepository).getIdByKey(eq(ENTITY_KEY), anyString());
    }

    @Test
    public void testInsertTimeline() {
        XmTimeline timeline = getTimeline();
        when(timelineRepository.insertTimelineByEntityAndDate(timeline)).thenReturn(true);
        when(timelineRepository.insertTimelineByEntityAndOperationAndDate(timeline)).thenReturn(true);
        when(timelineRepository.insertTimelineByUserAndDate(timeline)).thenReturn(true);
        when(timelineRepository.insertTimelineByUserAndOperationAndDate(timeline)).thenReturn(true);
        timelineCassandraService.insertTimelines(timeline);
        verify(timelineRepository).insertTimelineByEntityAndDate(timeline);
        verify(timelineRepository).insertTimelineByEntityAndOperationAndDate(timeline);
        verify(timelineRepository).insertTimelineByUserAndDate(timeline);
        verify(timelineRepository).insertTimelineByUserAndOperationAndDate(timeline);
    }

    private XmTimeline getTimeline() {
        XmTimeline timeline = new XmTimeline();
        timeline.setRid("id");
        timeline.setUserKey(USER_KEY);
        timeline.setEntityId(ENTITY_ID_LONG);
        timeline.setEntityKey(ENTITY_KEY);
        timeline.setStartDate(DATE);

        return timeline;
    }

}
