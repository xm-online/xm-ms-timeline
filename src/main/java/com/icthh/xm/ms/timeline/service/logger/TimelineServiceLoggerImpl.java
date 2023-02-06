package com.icthh.xm.ms.timeline.service.logger;

import static com.icthh.xm.ms.timeline.config.Constants.LOGGER_IMPL_CAPACITY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.domain.ext.IdOrKey;
import com.icthh.xm.ms.timeline.service.TimelineService;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

@Slf4j
public class TimelineServiceLoggerImpl implements TimelineService {

    private static final ArrayBlockingQueue<XmTimeline> timelines = new ArrayBlockingQueue<>(LOGGER_IMPL_CAPACITY);

    @Override
    public TimelinePageVM getTimelines(String msName,
                                       String userKey,
                                       String idOrKey,
                                       Instant dateFrom,
                                       Instant dateTo,
                                       String operation,
                                       String next,
                                       int limit,
                                       Sort sort) {

        // filter and return timelines from memory

        List<XmTimeline> filteredTimelines = timelines.stream()
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
            .filter(t -> dateFrom == null || (dateFrom.isBefore(t.getStartDate()) || dateFrom.equals(t.getStartDate())))
            .filter(t -> dateTo == null || (dateTo.isAfter(t.getStartDate()) || dateTo.equals(t.getStartDate())))
            .collect(Collectors.toList());

        return new TimelinePageVM(filteredTimelines, null);
    }

    @Override
    public void insertTimelines(XmTimeline xmTimeline) {
        timelines.add(xmTimeline);
        log.info("Event {}", xmTimeline);
    }

    @Override
    public void insertTimelines(DomainEvent domainEvent) {
        log.warn("Not implemented!");
    }

    private static boolean stringFilter(String filter, String value) {
        return !isNotBlank(filter) || StringUtils.equalsIgnoreCase(filter, value);
    }
}
