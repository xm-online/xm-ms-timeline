package com.icthh.xm.ms.timeline.service.timeline;

import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.domain.ext.IdOrKey;
import com.icthh.xm.ms.timeline.repository.cassandra.EntityMappingRepository;
import com.icthh.xm.ms.timeline.repository.cassandra.TimelineRepository;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;

public class TimelineServiceCassandraImpl implements TimelineService {
    private TimelineRepository timelineRepository;
    private EntityMappingRepository entityMappingRepository;
    private TenantContextHolder tenantContextHolder;

    public TimelineServiceCassandraImpl(
                    TimelineRepository timelineRepository,
                    EntityMappingRepository entityMappingRepository,
                    TenantContextHolder tenantContextHolder) {
        this.timelineRepository = timelineRepository;
        this.entityMappingRepository = entityMappingRepository;
        this.tenantContextHolder = tenantContextHolder;
    }

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
    @Override
    public TimelinePageVM getTimelines(String msName,
                                       String userKey,
                                       String idOrKey,
                                       Instant dateFrom,
                                       Instant dateTo,
                                       String operation,
                                       String next,
                                       int limit) {

        if (idOrKey != null) {
            IdOrKey idOrKeyObj = IdOrKey.of(idOrKey);

            Long id;

            if (idOrKeyObj.isId()) {
                id = idOrKeyObj.getId();
            } else {
                id = entityMappingRepository.getIdByKey(idOrKeyObj.getKey(),
                                TenantContextUtils.getRequiredTenantKeyValue(tenantContextHolder));
            }

            if (StringUtils.isNotBlank(operation)) {
                return timelineRepository.getTimelinesByEntityAndOpAndDate(
                    id, operation, dateFrom, dateTo, next, limit, msName);
            }
            return timelineRepository.getTimelinesByEntityAndDate(id, dateFrom, dateTo, next, limit, msName);

        }

        if (StringUtils.isNotBlank(operation)) {
            return timelineRepository.getTimelinesByUserKeyAndOpAndDate(userKey, operation,
                            dateFrom, dateTo, next, limit, msName);
        }
        return timelineRepository.getTimelinesByUserKeyAndDate(userKey, dateFrom, dateTo, next, limit, msName);
    }

    /**
     * Insert timelines.
     * @param xmTimeline  the timeline
     */
    @Override
    public void insertTimelines(XmTimeline xmTimeline) {
        insertIdAndKey(xmTimeline);
        if (StringUtils.isBlank(xmTimeline.getOperationName())) {
            xmTimeline.setOperationName(xmTimeline.getMsName() + ":" + xmTimeline.getHttpMethod() + ":"
                + xmTimeline.getOperationUrl());
        }
        insertUserKeyTimeline(xmTimeline);
        insertEntityTimeline(xmTimeline);
    }

    private void insertIdAndKey(XmTimeline xmTimeline) {
        if (xmTimeline.getEntityId() != null && StringUtils.isNotBlank(xmTimeline.getEntityKey())) {
            entityMappingRepository.insertKeyById(xmTimeline.getEntityId(), xmTimeline.getEntityKey(),
                xmTimeline.getTenant());

        } else if (xmTimeline.getEntityId() != null) {
            String key = entityMappingRepository.getKeyById(xmTimeline.getEntityId(), xmTimeline.getTenant());
            xmTimeline.setEntityKey(key);
        } else if (StringUtils.isNotBlank(xmTimeline.getEntityKey())) {
            Long id = entityMappingRepository.getIdByKey(xmTimeline.getEntityKey(), xmTimeline.getTenant());
            xmTimeline.setEntityId(id);
        }
    }


    private void insertUserKeyTimeline(XmTimeline xmTimeline) {
        if (StringUtils.isNotBlank(xmTimeline.getUserKey())) {
            timelineRepository.insertTimelineByUserAndDate(xmTimeline);
            timelineRepository.insertTimelineByUserAndOperationAndDate(xmTimeline);
        }
    }

    private void insertEntityTimeline(XmTimeline xmTimeline) {
        if (xmTimeline.getEntityId() != null) {
            timelineRepository.insertTimelineByEntityAndDate(xmTimeline);
            timelineRepository.insertTimelineByEntityAndOperationAndDate(xmTimeline);
        }
    }

}
