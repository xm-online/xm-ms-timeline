package com.icthh.xm.ms.timeline.service.db;

import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.domain.properties.TenantProperties;
import com.icthh.xm.ms.timeline.repository.jpa.TimelineJpaRepository;
import com.icthh.xm.ms.timeline.service.SortProcessor;
import com.icthh.xm.ms.timeline.service.TenantPropertiesService;
import com.icthh.xm.ms.timeline.service.TimelineService;
import com.icthh.xm.ms.timeline.service.dto.Timeline;
import com.icthh.xm.ms.timeline.service.mapper.XmTimelineMapper;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.icthh.xm.ms.timeline.service.db.JpaSpecUtil.*;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@AllArgsConstructor
public class TimelineServiceDbImpl implements TimelineService {

    private TimelineJpaRepository timelineRepository;
    private final XmTimelineMapper xmTimelineMapper;
    private TenantPropertiesService tenantPropertiesService;
    private SortProcessor sortProcessor;

    private static final String FIELD_START_DATE = "startDate";
    private static final String FIELD_MS_NAME = "msName";
    private static final String FIELD_USER_KEY = "userKey";
    private static final String FIELD_AGGREGATE_ID = "aggregateId";
    private static final String FIELD_OPERATION_NAME = "operationName";
    private static final String FIELD_SOURCE = "source";

    @Override
    public void insertTimelines(XmTimeline timeline) {
        timelineRepository.save(timeline);
    }

    @Transactional(readOnly = true)
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
        int page = next != null ? Integer.parseInt(next) : ZERO.intValue();
        PageRequest pageRequest = PageRequest.of(
            page,
            limit,
            sortProcessor.findValidOrDefault(XmTimeline.class, sort, Sort.by(DESC, FIELD_START_DATE))
        );

        Page<XmTimeline> timelines = getTimelines(msName, userKey, idOrKey, dateFrom, dateTo, operation, source, pageRequest);

        List<XmTimeline> content = filterResult(timelines);

        return new TimelinePageVM(xmTimelineMapper.xmTimelineToTimelineEvent(content), timelines.hasNext() ? String.valueOf(page + ONE.intValue()) : null);
    }

    @Override
    public void insertTimelines(DomainEvent domainEvent) {
        log.warn("Not implemented!");
    }

    @Transactional(readOnly = true)
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
        Pageable pageable = PageRequest.of(
            page,
            size,
            sortProcessor.findValidOrDefault(XmTimeline.class, sort, Sort.by(DESC, FIELD_START_DATE))
        );

        Page<XmTimeline> timelines = getTimelines(msName, userKey, aggregateId, dateFrom, dateTo, operation, source, pageable);

        List<XmTimeline> content = filterResult(timelines);

        return new PageImpl<>(xmTimelineMapper.xmTimelineToTimeline(content), timelines.getPageable(), timelines.getTotalElements());
    }

    private Page<XmTimeline> getTimelines(String msName,
                                          String userKey,
                                          String aggregateId,
                                          Instant dateFrom,
                                          Instant dateTo,
                                          String operation,
                                          String source,
                                          Pageable pageable) {
        Specification<XmTimeline> specificationsForFiltering = null;

        if (StringUtils.isNotBlank(msName)) {
            specificationsForFiltering =
                combineEqualSpecifications(specificationsForFiltering, msName, FIELD_MS_NAME);
        }
        if (StringUtils.isNotBlank(userKey)) {
            specificationsForFiltering =
                combineEqualSpecifications(specificationsForFiltering, userKey, FIELD_USER_KEY);
        }
        if (StringUtils.isNotBlank(operation)) {
            specificationsForFiltering =
                combineEqualSpecifications(specificationsForFiltering, operation, FIELD_OPERATION_NAME);
        }
        if (StringUtils.isNotBlank(source)) {
            specificationsForFiltering =
                combineEqualSpecifications(specificationsForFiltering, source, FIELD_SOURCE);
        }
        if (Objects.nonNull(dateFrom)) {
            specificationsForFiltering =
                combineGreaterThanOrEqualToSpecifications(specificationsForFiltering, dateFrom, FIELD_START_DATE);
        }
        if (Objects.nonNull(dateTo)) {
            specificationsForFiltering =
                combineLessThanOrEqualToSpecifications(specificationsForFiltering, dateTo, FIELD_START_DATE);
        }
        if (StringUtils.isNotBlank(aggregateId)) {
            specificationsForFiltering =
                combineEqualSpecifications(specificationsForFiltering, aggregateId, FIELD_AGGREGATE_ID);
        }

        return specificationsForFiltering != null
            ? timelineRepository.findAllWithHeaders(specificationsForFiltering, pageable)
            : timelineRepository.findAllWithHeaders(pageable);
    }

    /**
     * TODO temporary solution, best way to handle this case: redesign of database.
     * All @Lob fields should be moved to separate tables.
     * Than Lazy load + NamedEntityGraph should be used
     *
     * @param timelines result of timeline search query
     * @return filtered result
     */
    private List<XmTimeline> filterResult(Page<XmTimeline> timelines) {
        List<XmTimeline> content = timelines.getContent();

        if (hidePayload()) {
            content.forEach(xmTimeline -> {
                xmTimeline.setResponseBody(null);
                xmTimeline.setRequestBody(null);
            });
        }

        return content;
    }

    /**
     * Hide response body if hide-payload parameter set to true.
     *
     * @return true if passes payload condition
     */
    private boolean hidePayload() {
        return Optional.ofNullable(tenantPropertiesService.getTenantProps())
            .map(TenantProperties::getEvent)
            .map(TenantProperties.Event::getHidePayload)
            .orElse(Boolean.TRUE);
    }
}
