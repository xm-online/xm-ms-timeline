package com.icthh.xm.ms.timeline.service.db;

import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.domain.properties.TenantProperties;
import com.icthh.xm.ms.timeline.repository.jpa.TimelineJpaRepository;
import com.icthh.xm.ms.timeline.service.SortProcessor;
import com.icthh.xm.ms.timeline.service.TenantPropertiesService;
import com.icthh.xm.ms.timeline.service.TimelineService;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private TenantPropertiesService tenantPropertiesService;
    private SortProcessor sortProcessor;

    private static final String FIELD_START_DATE = "startDate";
    private static final String FIELD_MS_NAME = "msName";
    private static final String FIELD_USER_KEY = "userKey";
    private static final String FIELD_ENTITY_ID = "entityId";
    private static final String FIELD_OPERATION_NAME = "operationName";

    @Override
    public void insertTimelines(XmTimeline timeline) {
        timelineRepository.save(timeline);
    }

    @Transactional
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
        if (Objects.nonNull(dateFrom)) {
            specificationsForFiltering =
                combineGreaterThanOrEqualToSpecifications(specificationsForFiltering, dateFrom, FIELD_START_DATE);
        }
        if (Objects.nonNull(dateTo)) {
            specificationsForFiltering =
                combineLessThanOrEqualToSpecifications(specificationsForFiltering, dateTo, FIELD_START_DATE);
        }
        if (StringUtils.isNumeric(idOrKey)) {
            specificationsForFiltering =
                combineEqualSpecifications(specificationsForFiltering, idOrKey, FIELD_ENTITY_ID);
        }

        int page = next != null ? Integer.parseInt(next) : ZERO.intValue();
        PageRequest pageRequest = PageRequest.of(
            page,
            limit,
            sortProcessor.findValidOrDefault(XmTimeline.class, sort, Sort.by(DESC, FIELD_START_DATE))
        );

        Page<XmTimeline> timelines = specificationsForFiltering != null
            ? timelineRepository.findAllWithHeaders(specificationsForFiltering, pageRequest)
            : timelineRepository.findAllWithHeaders(pageRequest);

        List<XmTimeline> content = filterResult(timelines);

        return new TimelinePageVM(content, timelines.hasNext() ? String.valueOf(page + ONE.intValue()) : null);
    }

    @Override
    public void insertTimelines(DomainEvent domainEvent) {
        log.warn("Not implemented!");
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
