package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;

import java.time.Instant;

public interface TimelineService {

    /**
     * Get page with timelines.
     *
     * @param userKey   the user key
     * @param idOrKey   the entity id or key
     * @param dateFrom  the date from
     * @param dateTo    the date to
     * @param operation the operation
     * @param next      the next page code
     * @param limit     the limit per page
     * @return page with timelines and next page code
     */
    TimelinePageVM getTimelines(String msName,
                                String userKey,
                                String idOrKey,
                                Instant dateFrom,
                                Instant dateTo,
                                String operation,
                                String next,
                                int limit);

    /**
     * Insert timelines.
     *
     * @param xmTimeline the
     *                   timeline
     */
    void insertTimelines(XmTimeline xmTimeline);
}
