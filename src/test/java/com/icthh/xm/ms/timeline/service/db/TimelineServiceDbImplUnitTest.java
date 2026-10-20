package com.icthh.xm.ms.timeline.service.db;

import com.icthh.xm.ms.timeline.AbstractUnitTest;
import com.icthh.xm.ms.timeline.repository.jpa.TimelineJpaRepository;
import com.icthh.xm.ms.timeline.service.SortProcessor;
import com.icthh.xm.ms.timeline.service.TenantPropertiesService;
import com.icthh.xm.ms.timeline.service.mapper.XmTimelineMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimelineServiceDbImplUnitTest extends AbstractUnitTest {

    private static final int MAX_PAGE_SIZE = 1000;

    @Mock
    private TimelineJpaRepository timelineRepository;

    @Mock
    private XmTimelineMapper xmTimelineMapper;

    @Mock
    private TenantPropertiesService tenantPropertiesService;

    @Mock
    private SortProcessor sortProcessor;

    private TimelineServiceDbImpl service;

    @BeforeEach
    void setUp() {
        service = new TimelineServiceDbImpl(timelineRepository, xmTimelineMapper, tenantPropertiesService,
            sortProcessor, MAX_PAGE_SIZE);
        when(sortProcessor.findValidOrDefault(any(), any(), any())).thenReturn(Sort.unsorted());
        when(timelineRepository.findAllWithHeaders(any(Pageable.class)))
            .thenAnswer(invocation -> Page.empty(invocation.getArgument(0)));
    }

    @Test
    void limitIsCappedByMaxPageSize() {
        service.getTimelines(null, null, null, null, null, null, null, null, null, Integer.MAX_VALUE, Sort.unsorted());

        assertThat(requestedPageSize()).isEqualTo(MAX_PAGE_SIZE);
    }

    @Test
    void sizeIsCappedByMaxPageSize() {
        service.getTimelines(null, null, null, null, null, null, null, null, null, 0, Integer.MAX_VALUE,
            Sort.unsorted());

        assertThat(requestedPageSize()).isEqualTo(MAX_PAGE_SIZE);
    }

    @Test
    void sizeBelowMaxPageSizeIsKept() {
        service.getTimelines(null, null, null, null, null, null, null, null, null, 0, 20, Sort.unsorted());

        assertThat(requestedPageSize()).isEqualTo(20);
    }

    private int requestedPageSize() {
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(timelineRepository).findAllWithHeaders(captor.capture());
        return captor.getValue().getPageSize();
    }
}
