package com.icthh.xm.ms.timeline.config;

import static com.icthh.xm.ms.timeline.config.Constants.CASSANDRA_IMPL;
import static com.icthh.xm.ms.timeline.config.Constants.DB_IMPL;
import static com.icthh.xm.ms.timeline.config.Constants.LOGGER_IMPL;

import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.ms.timeline.repository.cassandra.EntityMappingRepository;
import com.icthh.xm.ms.timeline.repository.cassandra.TimelineCassandraRepository;
import com.icthh.xm.ms.timeline.repository.jpa.TimelineJpaRepository;

import com.icthh.xm.ms.timeline.service.timeline.TimelineService;
import com.icthh.xm.ms.timeline.service.timeline.TimelineServiceCassandraImpl;
import com.icthh.xm.ms.timeline.service.timeline.TimelineServiceDbImpl;
import com.icthh.xm.ms.timeline.service.timeline.TimelineServiceLoggerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

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
    @ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = DB_IMPL)
    public TimelineService dbTimelineService(TimelineJpaRepository timelineJpaRepository) {
        return new TimelineServiceDbImpl(timelineJpaRepository);
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
