package com.icthh.xm.ms.timeline.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icthh.xm.commons.logging.util.MdcUtils;
import com.icthh.xm.commons.messaging.event.system.SystemEvent;
import com.icthh.xm.commons.security.XmAuthenticationContextHolder;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.lep.api.LepManager;
import com.icthh.xm.ms.timeline.service.SystemQueueProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import static com.icthh.xm.commons.lep.XmLepConstants.THREAD_CONTEXT_KEY_AUTH_CONTEXT;
import static com.icthh.xm.commons.lep.XmLepConstants.THREAD_CONTEXT_KEY_TENANT_CONTEXT;


@Slf4j
@Service
@RequiredArgsConstructor
public class SystemQueueConsumer {

    private final ObjectMapper objectMapper;
    private final SystemQueueProcessorService systemQueueProcessorService;

    private final TenantContextHolder tenantContextHolder;
    private final XmAuthenticationContextHolder authContextHolder;
    private final LepManager lepManager;

    /**
     * Consume tenant command event message.
     *
     * @param message the tenant command event message
     */
    @Retryable(maxAttemptsExpression = "${application.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${application.retry.delay}",
                    multiplierExpression = "${application.retry.multiplier}"))
    public void consumeEvent(ConsumerRecord<String, String> message) {
        MdcUtils.putRid();
        try {
            SystemEvent event = fromJson(message.value());
            init(event.getTenantKey());
            systemQueueProcessorService.handleSystemEvent(event);
        } finally {
            destroy();
        }
    }

    private void init(String tenantKey) {
        if (StringUtils.isNotBlank(tenantKey)) {
            TenantContextUtils.setTenant(tenantContextHolder, tenantKey);

            lepManager.beginThreadContext(threadContext -> {
                threadContext.setValue(THREAD_CONTEXT_KEY_TENANT_CONTEXT, tenantContextHolder.getContext());
                threadContext.setValue(THREAD_CONTEXT_KEY_AUTH_CONTEXT, authContextHolder.getContext());
            });
        }
    }

    private void destroy() {
        lepManager.endThreadContext();
        tenantContextHolder.getPrivilegedContext().destroyCurrentContext();
        MdcUtils.removeRid();
    }

    private SystemEvent fromJson(String value) {
        try {
            return objectMapper.readValue(value, SystemEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
