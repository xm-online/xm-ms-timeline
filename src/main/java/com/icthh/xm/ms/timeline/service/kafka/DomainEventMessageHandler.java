package com.icthh.xm.ms.timeline.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import com.icthh.xm.commons.lep.api.LepManagementService;
import com.icthh.xm.commons.security.XmAuthenticationContextHolder;
import com.icthh.xm.commons.tenant.PlainTenant;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.commons.tenant.TenantKey;
import com.icthh.xm.commons.topic.domain.TopicConfig;
import com.icthh.xm.commons.topic.message.MessageHandler;
import com.icthh.xm.ms.timeline.service.DomainEventService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static com.icthh.xm.commons.lep.XmLepConstants.THREAD_CONTEXT_KEY_AUTH_CONTEXT;
import static com.icthh.xm.commons.lep.XmLepConstants.THREAD_CONTEXT_KEY_TENANT_CONTEXT;

@Slf4j
@RequiredArgsConstructor
public class DomainEventMessageHandler implements MessageHandler {

    private final ObjectMapper objectMapper;
    private final DomainEventService domainEventService;
    private final TenantContextHolder tenantContextHolder;
    private final XmAuthenticationContextHolder authContextHolder;
    private final LepManagementService lepManagementService;

    @Override
    public void onMessage(String message, String tenant, TopicConfig topicConfig) {

        tenantContextHolder.getPrivilegedContext().execute(TenantContextUtils.buildTenant(tenant), () -> {
            try (var context = lepManagementService.beginThreadContext()) {
                DomainEvent domainEvent = fromJson(message);
                domainEventService.processEvent(domainEvent);
            }
        });


    }



    @SneakyThrows
    private DomainEvent fromJson(String json) {
        return objectMapper.readValue(json, DomainEvent.class);
    }
}
