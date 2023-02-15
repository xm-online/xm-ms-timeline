package com.icthh.xm.ms.timeline.service.logger;

import static com.icthh.xm.ms.timeline.config.Constants.LOGGER_IMPL_CAPACITY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.service.TimelineService;
import com.icthh.xm.ms.timeline.service.dto.Timeline;
import com.icthh.xm.ms.timeline.service.mapper.XmTimelineMapper;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

@Slf4j
@RequiredArgsConstructor
public class TimelineServiceLoggerImpl implements TimelineService {

    private final ArrayBlockingQueue<XmTimeline> timelines = new ArrayBlockingQueue<>(LOGGER_IMPL_CAPACITY);

    private final XmTimelineMapper xmTimelineMapper;

    @Override
    public TimelinePageVM getTimelines(String msName,
                                       String userKey,
                                       String idOrKey,
                                       Instant dateFrom,
                                       Instant dateTo,
                                       String operation,
                                       String source,
                                       String next,
                                       int limit,
                                       Sort sort) {
        List<XmTimeline> filteredTimelines = getTimelines(msName, userKey, idOrKey, dateFrom, dateTo, operation, source);
        return new TimelinePageVM(xmTimelineMapper.xmTimelineToTimelineEvent(filteredTimelines), null);
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

    @Override
    public Page<Timeline> getTimelines(String msName,
                                       String userKey,
                                       String aggregateId,
                                       Instant dateFrom,
                                       Instant dateTo,
                                       String operation,
                                       String source,
                                       int page,
                                       int size,
                                       Sort sort) {
        List<XmTimeline> filteredTimelines = getTimelines(msName, userKey, aggregateId, dateFrom, dateTo, operation, source);
        return new PageImpl<>(xmTimelineMapper.xmTimelineToTimeline(filteredTimelines));
    }

    private List<XmTimeline> getTimelines(String msName,
                                       String userKey,
                                       String aggregateId,
                                       Instant dateFrom,
                                       Instant dateTo,
                                       String operation,
                                       String source) {
        // filter and return timelines from memory

        return timelines.stream()
            .filter(t -> stringFilter(msName, t.getMsName()))
            .filter(t -> stringFilter(userKey, t.getUserKey()))
            .filter(t -> stringFilter(operation, t.getOperationName()))
            .filter(t -> stringFilter(source, t.getSource()))
            .filter(t -> {
                if (isNotBlank(aggregateId)) {
                    return aggregateId.equals(t.getAggregateId());
                }

                return true;
            })
            .filter(t -> dateFrom == null || (dateFrom.isBefore(t.getStartDate()) || dateFrom.equals(t.getStartDate())))
            .filter(t -> dateTo == null || (dateTo.isAfter(t.getStartDate()) || dateTo.equals(t.getStartDate())))
            .collect(Collectors.toList());
    }

    private static boolean stringFilter(String filter, String value) {
        return !isNotBlank(filter) || StringUtils.equalsIgnoreCase(filter, value);
    }
}
