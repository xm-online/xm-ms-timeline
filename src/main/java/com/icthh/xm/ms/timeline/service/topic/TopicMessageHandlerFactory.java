package com.icthh.xm.ms.timeline.service.topic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icthh.xm.commons.security.XmAuthenticationContextHolder;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.lep.api.LepManager;
import com.icthh.xm.ms.timeline.service.DomainEventService;
import com.icthh.xm.ms.timeline.service.kafka.DomainEventMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TopicMessageHandlerFactory {

    private final ObjectMapper objectMapper;
    private final DomainEventService domainEventService;
    private final TenantContextHolder tenantContextHolder;
    private final XmAuthenticationContextHolder authContextHolder;
    private final LepManager lepManager;

    public DomainEventMessageHandler createTopicMessageHandler() {
        return new DomainEventMessageHandler(objectMapper, domainEventService, tenantContextHolder, authContextHolder, lepManager);
    }
}
