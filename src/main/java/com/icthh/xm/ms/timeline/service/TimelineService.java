package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;
import org.springframework.data.domain.Sort;

import java.time.Instant;

public interface TimelineService {

    TimelinePageVM getTimelines(String msName,
                                String userKey,
                                String idOrKey,
                                Instant dateFrom,
                                Instant dateTo,
                                String operation,
                                String next,
                                int limit,
                                Sort sort);

    void insertTimelines(XmTimeline xmTimeline);

    void insertTimelines(DomainEvent domainEvent);
}
