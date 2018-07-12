package com.icthh.xm.ms.timeline.config;

import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.ms.timeline.repository.cassandra.EntityMappingRepository;
import com.icthh.xm.ms.timeline.repository.cassandra.TimelineRepository;
import com.icthh.xm.ms.timeline.service.timeline.TimelineService;
import com.icthh.xm.ms.timeline.service.timeline.TimelineServiceCassandraImpl;
import com.icthh.xm.ms.timeline.service.timeline.TimelineServiceLoggerImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.icthh.xm")
@RequiredArgsConstructor
public class ServiceConfiguration {

    private final ApplicationProperties applicationProperties;

    private static final String CASSANDRA_IMPL = "cassandra";
    private static final String LOGGER_IMPL = "logger";
    private static final String H2DB_IMPL = "h2db";
    private static final String POSTGRES_IMPL = "postgresdb";

    @Bean
    public TimelineService timelineService(TimelineRepository timelineRepository,
                                              EntityMappingRepository entityMappingRepository,
                                              TenantContextHolder tenantContextHolder) {

        switch (applicationProperties.getTimelineServiceImpl()) {
            case CASSANDRA_IMPL: return new TimelineServiceCassandraImpl(timelineRepository, entityMappingRepository, tenantContextHolder);
            case LOGGER_IMPL: return new TimelineServiceLoggerImpl();
            case H2DB_IMPL: throw new NotImplementedException("Strategy " + applicationProperties.getTimelineServiceImpl() + " not implemented");
            case POSTGRES_IMPL: throw new NotImplementedException("Strategy " + applicationProperties.getTimelineServiceImpl() + " not implemented");
            default: return new TimelineServiceCassandraImpl(timelineRepository, entityMappingRepository, tenantContextHolder);
        }
    }

}
