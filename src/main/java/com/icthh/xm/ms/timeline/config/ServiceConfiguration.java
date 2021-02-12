package com.icthh.xm.ms.timeline.config;

import static com.icthh.xm.ms.timeline.config.Constants.CASSANDRA_IMPL;
import static com.icthh.xm.ms.timeline.config.Constants.LOGGER_IMPL;
import static com.icthh.xm.ms.timeline.config.Constants.RDBMS_IMPL;

import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.ms.timeline.repository.cassandra.EntityMappingRepository;
import com.icthh.xm.ms.timeline.repository.cassandra.TimelineCassandraRepository;
import com.icthh.xm.ms.timeline.repository.jpa.LazyLoadTimelineJpaRepository;
import com.icthh.xm.ms.timeline.repository.jpa.TimelineJpaRepository;
import com.icthh.xm.ms.timeline.service.TenantPropertiesService;
import com.icthh.xm.ms.timeline.service.TimelineService;
import com.icthh.xm.ms.timeline.service.cassandra.TimelineServiceCassandraImpl;
import com.icthh.xm.ms.timeline.service.db.TimelineServiceDbImpl;
import com.icthh.xm.ms.timeline.service.logger.TimelineServiceLoggerImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ServiceConfiguration {

    private final TenantPropertiesService tenantPropertiesService;

    @Bean(name = "timelineService")
    @ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = CASSANDRA_IMPL)
    public TimelineService cassandraTimelineService(TimelineCassandraRepository tr,
                                                    EntityMappingRepository emr,
                                                    TenantContextHolder tch) {
        return new TimelineServiceCassandraImpl(tr, emr, tch);
    }

    @Bean(name = "timelineService")
    @ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = LOGGER_IMPL)
    public TimelineService loggerTimelineService() {
        return new TimelineServiceLoggerImpl();
    }

    @Bean(name = "timelineService")
    @ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = RDBMS_IMPL)
    public TimelineService dbTimelineService(
        TimelineJpaRepository timelineJpaRepository,
        LazyLoadTimelineJpaRepository lazyLoadTimelineJpaRepository) {
        return new TimelineServiceDbImpl(lazyLoadTimelineJpaRepository, timelineJpaRepository, tenantPropertiesService);
    }

    @Bean(name = "timelineService")
    @ConditionalOnProperty(name = "application.timeline-service-impl", matchIfMissing = true)
    @ConditionalOnMissingBean
    public TimelineService defaultTimelineService(TimelineCassandraRepository tr,
                                                  EntityMappingRepository emr,
                                                  TenantContextHolder tch) {
        return new TimelineServiceCassandraImpl(tr, emr, tch);
    }
}
