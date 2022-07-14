package com.icthh.xm.ms.timeline.service.cassandra;

import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.domain.ext.IdOrKey;
import com.icthh.xm.ms.timeline.repository.cassandra.EntityMappingRepository;
import com.icthh.xm.ms.timeline.repository.cassandra.TimelineCassandraRepository;
import com.icthh.xm.ms.timeline.service.TimelineService;
import com.icthh.xm.ms.timeline.web.rest.vm.TimelinePageVM;

import java.time.Instant;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

public class TimelineServiceCassandraImpl implements TimelineService {
    private TimelineCassandraRepository timelineRepository;
    private EntityMappingRepository entityMappingRepository;
    private TenantContextHolder tenantContextHolder;

    public TimelineServiceCassandraImpl(
        TimelineCassandraRepository timelineRepository,
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
                                       int limit,
                                       Sort sort) {

        if (idOrKey != null) {
            IdOrKey idOrKeyObj = IdOrKey.of(idOrKey);

            Long id = idOrKeyObj.isId() ? idOrKeyObj.getId() :
                entityMappingRepository.getIdByKey(idOrKeyObj.getKey(),
                    TenantContextUtils.getRequiredTenantKeyValue(tenantContextHolder));

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
     *
     * @param xmTimeline the timeline
     */
    @Override
    public void insertTimelines(XmTimeline xmTimeline) {
        insertIdAndKey(xmTimeline);
        if (StringUtils.isBlank(xmTimeline.getOperationName())) {
            xmTimeline.setOperationName(buildOperationName(xmTimeline));
        }
        insertUserKeyTimeline(xmTimeline);
        insertEntityTimeline(xmTimeline);
    }

    private String buildOperationName(XmTimeline xmTimeline) {
        return xmTimeline.getMsName() + ":" + xmTimeline.getHttpMethod() + ":" + xmTimeline.getOperationUrl();
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
