package com.icthh.xm.ms.timeline.service.timeline;

import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.domain.ext.IdOrKey;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Slf4j
public class TimelineServiceLoggerImpl implements TimelineService {

    private ArrayBlockingQueue<XmTimeline> timelines = new ArrayBlockingQueue(100);

    private static boolean stringFilter(String filter, String value) {
        return isNotBlank(filter) ? StringUtils.equalsIgnoreCase(filter, value) : true;
    }

    @Override
    public TimelinePageVM getTimelines(String msName, String userKey, String idOrKey, Instant dateFrom, Instant dateTo, String operation, String next, int limit) {

        // filter and return timelines from memory

        List filteredTimelines = timelines.stream()
            .filter(t -> stringFilter(msName, t.getMsName()))
            .filter(t -> stringFilter(userKey, t.getUserKey()))
            .filter(t -> stringFilter(operation, t.getOperationName()))
            .filter(t -> {
                if (isNotBlank(idOrKey)) {
                    IdOrKey idOrKeyObj = IdOrKey.of(idOrKey);

                    if (idOrKeyObj.isId()) {
                        return idOrKeyObj.getId().equals(t.getEntityId());
                    }
                }

                return true;
            })
            .filter(t -> dateFrom != null ? dateFrom.isBefore(t.getStartDate()) || dateFrom.equals(t.getStartDate()) : true)
            .filter(t -> dateTo != null ? dateTo.isAfter(t.getStartDate()) || dateTo.equals(t.getStartDate()) : true)
            .collect(Collectors.toList());

        return new TimelinePageVM(filteredTimelines, null);
    }

    @Override
    public void insertTimelines(XmTimeline xmTimeline) {
        timelines.add(xmTimeline);
        log.info("Event {}", xmTimeline);
    }
}
