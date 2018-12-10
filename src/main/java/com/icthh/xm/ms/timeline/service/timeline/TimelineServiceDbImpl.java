package com.icthh.xm.ms.timeline.service.timeline;

import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.repository.jpa.TimelineJpaRepository;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;

import java.time.Instant;
import java.util.Objects;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

@AllArgsConstructor
public class TimelineServiceDbImpl implements TimelineService {

    private TimelineJpaRepository timelineRepository;

    private static <T> Specification<XmTimeline> equalSpecification(T filterValue, String propertyName) {
        return (root, query, builder) -> builder.equal(root.get(propertyName), filterValue);
    }

    private static Specification<XmTimeline> lessThanOrEqualToSpecification(Instant filterValue, String propertyName) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get(propertyName), filterValue);
    }

    private static Specification<XmTimeline> greaterThanOrEqualToSpecification(Instant filterValue, String propertyName) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get(propertyName), filterValue);
    }

    private Specifications combineEqualSpecifications(Specifications prevSpec, String filterValue, String propertyName) {
        Specifications specifications = Specifications.where(equalSpecification(filterValue, propertyName));
        return prevSpec != null ? prevSpec.and(specifications) : specifications;
    }

    private Specifications combineLessThanOrEqualToSpecifications(Specifications prevSpec, Instant filterValue, String propertyName) {
        Specifications specifications = Specifications.where(lessThanOrEqualToSpecification(filterValue, propertyName));
        return prevSpec != null ? prevSpec.and(specifications) : specifications;
    }

    private Specifications combineGreaterThanOrEqualToSpecifications(Specifications prevSpec, Instant filterValue, String propertyName) {
        Specifications specifications = Specifications.where(greaterThanOrEqualToSpecification(filterValue, propertyName));
        return prevSpec != null ? prevSpec.and(specifications) : specifications;
    }

    @Override
    public void insertTimelines(XmTimeline timeline) {
        timelineRepository.save(timeline);
    }

    @Override
    public TimelinePageVM getTimelines(String msName, String userKey, String idOrKey, Instant dateFrom, Instant dateTo, String operation, String next, int limit) {

        Specifications<XmTimeline> specificationsForFiltering = null;

        if (StringUtils.isNotBlank(msName)) {
            specificationsForFiltering = combineEqualSpecifications(specificationsForFiltering, msName, "msName");
        }
        if (StringUtils.isNotBlank(userKey)) {
            specificationsForFiltering = combineEqualSpecifications(specificationsForFiltering, userKey, "userKey");
        }
        if (StringUtils.isNotBlank(operation)) {
            specificationsForFiltering = combineEqualSpecifications(specificationsForFiltering, operation, "operationName");
        }
        if (Objects.nonNull(dateFrom)) {
            specificationsForFiltering = combineGreaterThanOrEqualToSpecifications(specificationsForFiltering, dateFrom, "startDate");
        }
        if (Objects.nonNull(dateTo)) {
            specificationsForFiltering = combineLessThanOrEqualToSpecifications(specificationsForFiltering, dateTo, "startDate");
        }
        if (StringUtils.isNumeric(idOrKey)) {
            specificationsForFiltering = combineEqualSpecifications(specificationsForFiltering, idOrKey, "entityId");
        }

        int page = next != null ? Integer.parseInt(next) : 0;

        PageRequest pageRequest = new PageRequest(page, limit == 0 ? 100 : limit);

        Page<XmTimeline> timelines = specificationsForFiltering != null ? timelineRepository.findAll(specificationsForFiltering, pageRequest) : timelineRepository.findAll(pageRequest);

        return new TimelinePageVM(timelines.getContent(), timelines.hasNext() ? String.valueOf(page + 1) : null);
    }
}
