package com.icthh.xm.ms.timeline.service.timeline;

import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.Instant;

public class TimelineServicePostgresDBImpl implements TimelineService {

    @Override
    public TimelinePageVM getTimelines(String msName, String userKey, String idOrKey, Instant dateFrom, Instant dateTo, String operation, String next, int limit) {
        throw new NotImplementedException();
    }

    @Override
    public void insertTimelines(XmTimeline xmTimeline) {
        throw new NotImplementedException();
    }
}
