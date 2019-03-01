package com.icthh.xm.ms.timeline.service.db;

import static com.icthh.xm.ms.timeline.service.db.JpaSpecUtil.combineEqualSpecifications;
import static com.icthh.xm.ms.timeline.service.db.JpaSpecUtil.combineGreaterThanOrEqualToSpecifications;
import static com.icthh.xm.ms.timeline.service.db.JpaSpecUtil.combineLessThanOrEqualToSpecifications;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.repository.jpa.TimelineJpaRepository;
import com.icthh.xm.ms.timeline.service.TimelineService;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;

import java.time.Instant;
import java.util.Objects;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;

@AllArgsConstructor
public class TimelineServiceDbImpl implements TimelineService {

    private TimelineJpaRepository timelineRepository;

    private static final String FIELD_START_DATE = "startDate";
    private static final String FIELD_MS_NAME = "msName";
    private static final String FIELD_USER_KEY = "userKey";
    private static final String FIELD_ENTITY_ID = "entityId";
    private static final String FIELD_OPERATION_NAME = "operationName";

    @Override
    public void insertTimelines(XmTimeline timeline) {
        timelineRepository.save(timeline);
    }

    @Override
    public TimelinePageVM getTimelines(String msName,
                                       String userKey,
                                       String idOrKey,
                                       Instant dateFrom,
                                       Instant dateTo,
                                       String operation,
                                       String next,
                                       int limit) {
        Specifications<XmTimeline> specificationsForFiltering = null;

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
        PageRequest pageRequest = new PageRequest(page, limit, Sort.Direction.DESC, "startDate");

        Page<XmTimeline> timelines = specificationsForFiltering != null
            ? timelineRepository.findAll(specificationsForFiltering, pageRequest)
            : timelineRepository.findAll(pageRequest);

        return new TimelinePageVM(timelines.getContent(),
            timelines.hasNext() ? String.valueOf(page + ONE.intValue()) : null);
    }
}
