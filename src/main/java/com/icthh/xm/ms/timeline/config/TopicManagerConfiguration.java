package com.icthh.xm.ms.timeline.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icthh.xm.commons.security.XmAuthenticationContextHolder;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.topic.message.MessageHandler;
import com.icthh.xm.lep.api.LepManager;
import com.icthh.xm.ms.timeline.service.DomainEventService;
import com.icthh.xm.ms.timeline.service.kafka.DomainEventMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TopicManagerConfiguration {

    private final ObjectMapper objectMapper;
    private final DomainEventService domainEventService;
    private final TenantContextHolder tenantContextHolder;
    private final XmAuthenticationContextHolder authContextHolder;
    private final LepManager lepManager;

    @Bean
    public MessageHandler messageHandler() {
        return new DomainEventMessageHandler(objectMapper, domainEventService, tenantContextHolder, authContextHolder, lepManager);
    }
}
