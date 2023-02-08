package com.icthh.xm.ms.timeline.config;

import static com.icthh.xm.ms.timeline.config.Constants.LOGGER_IMPL;
import static com.icthh.xm.ms.timeline.config.Constants.RDBMS_IMPL;

import com.icthh.xm.ms.timeline.repository.jpa.TimelineJpaRepository;
import com.icthh.xm.ms.timeline.service.SortProcessor;
import com.icthh.xm.ms.timeline.service.TenantPropertiesService;
import com.icthh.xm.ms.timeline.service.TimelineService;
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
    private final SortProcessor sortProcessor;

    @Bean(name = "timelineService")
    @ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = LOGGER_IMPL)
    public TimelineService loggerTimelineService() {
        return new TimelineServiceLoggerImpl();
    }

    @Bean(name = "timelineService")
    @ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = RDBMS_IMPL)
    public TimelineService dbTimelineService(TimelineJpaRepository timelineJpaRepository) {
        return new TimelineServiceDbImpl(timelineJpaRepository, tenantPropertiesService, sortProcessor);
    }
}
