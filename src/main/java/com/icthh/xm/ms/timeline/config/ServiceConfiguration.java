package com.icthh.xm.ms.timeline.config;

import static com.icthh.xm.ms.timeline.config.Constants.LOGGER_IMPL;
import static com.icthh.xm.ms.timeline.config.Constants.RDBMS_IMPL;

import com.icthh.xm.commons.permission.access.XmPermissionEvaluator;
import com.icthh.xm.ms.timeline.repository.jpa.TimelineJpaRepository;
import com.icthh.xm.ms.timeline.service.SortProcessor;
import com.icthh.xm.ms.timeline.service.TenantPropertiesService;
import com.icthh.xm.ms.timeline.service.TimelineService;
import com.icthh.xm.ms.timeline.service.db.TimelineServiceDbImpl;
import com.icthh.xm.ms.timeline.service.logger.TimelineServiceLoggerImpl;
import com.icthh.xm.ms.timeline.service.mapper.XmTimelineMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;

@Configuration
@RequiredArgsConstructor
public class ServiceConfiguration {

    private final TenantPropertiesService tenantPropertiesService;
    private final XmTimelineMapper xmTimelineMapper;
    private final SortProcessor sortProcessor;

    @Bean(name = "timelineService")
    @ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = LOGGER_IMPL)
    public TimelineService loggerTimelineService() {
        return new TimelineServiceLoggerImpl(xmTimelineMapper);
    }

    @Bean(name = "timelineService")
    @ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = RDBMS_IMPL)
    public TimelineService dbTimelineService(TimelineJpaRepository timelineJpaRepository) {
        return new TimelineServiceDbImpl(timelineJpaRepository, xmTimelineMapper, tenantPropertiesService, sortProcessor);
    }

    @Primary
    @Bean
    static MethodSecurityExpressionHandler expressionHandler(XmPermissionEvaluator customPermissionEvaluator) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(customPermissionEvaluator);
        return expressionHandler;
    }
}
