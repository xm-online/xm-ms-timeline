package com.icthh.xm.ms.timeline.config;

import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.ms.timeline.repository.cassandra.EntityMappingRepository;
import com.icthh.xm.ms.timeline.repository.cassandra.TimelineCassandraRepository;
import com.icthh.xm.ms.timeline.repository.jpa.TimelineJpaRepository;
import com.icthh.xm.ms.timeline.service.timeline.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

    public static final String CASSANDRA_IMPL = "cassandra";
    public static final String LOGGER_IMPL = "logger";
    public static final String H2DB_IMPL = "h2db";
    public static final String POSTGRES_IMPL = "postgresdb";

    @Bean(name = "timelineService")
    @ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = CASSANDRA_IMPL)
    public TimelineService cassandraTimelineService(TimelineCassandraRepository tr, EntityMappingRepository emr, TenantContextHolder tch) {
        return new TimelineServiceCassandraImpl(tr, emr, tch);
    }

    @Bean(name = "timelineService")
    @ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = LOGGER_IMPL)
    public TimelineService loggerTimelineService() {
        return new TimelineServiceLoggerImpl();
    }

    @Bean(name = "timelineService")
    @ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = H2DB_IMPL)
    public TimelineService h2dbTimelineService(TimelineJpaRepository timelineJpaRepository) {
        return new TimelineServiceH2dbImpl(timelineJpaRepository);
    }

    @Bean(name = "timelineService")
    @ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = POSTGRES_IMPL)
    public TimelineService postgresDBTimelineService() {
        return new TimelineServicePostgresDBImpl();
    }

    @Bean(name = "timelineService")
    @ConditionalOnProperty(name = "application.timeline-service-impl", matchIfMissing = true)
    @ConditionalOnMissingBean
    public TimelineService defaultTimelineService(TimelineCassandraRepository tr, EntityMappingRepository emr, TenantContextHolder tch) {
        return new TimelineServiceCassandraImpl(tr, emr, tch);
    }
}
