package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;

import java.time.Instant;

public interface TimelineService {

    TimelinePageVM getTimelines(String msName,
                                String userKey,
                                String idOrKey,
                                Instant dateFrom,
                                Instant dateTo,
                                String operation,
                                String next,
                                int limit);

    TimelinePageVM getTimelines(String msName,
                                String userKey,
                                String idOrKey,
                                Instant dateFrom,
                                Instant dateTo,
                                String operation,
                                String next,
                                int limit,
                                boolean withoutHeaders);

    void insertTimelines(XmTimeline xmTimeline);
}
