package com.icthh.xm.ms.timeline.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icthh.xm.commons.lep.api.LepManagementService;
import com.icthh.xm.commons.logging.util.MdcUtils;
import com.icthh.xm.commons.messaging.event.system.SystemEvent;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.ms.timeline.service.SystemQueueProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemQueueConsumer {

    private final ObjectMapper objectMapper;
    private final SystemQueueProcessorService systemQueueProcessorService;

    private final TenantContextHolder tenantContextHolder;
    private final LepManagementService lepManagementService;

    /**
     * Consume tenant command event message.
     *
     * @param message the tenant command event message
     */
    @Retryable(maxAttemptsExpression = "${application.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${application.retry.delay}",
                    multiplierExpression = "${application.retry.multiplier}"))
    public void consumeEvent(ConsumerRecord<String, String> message) {
        try {
            SystemEvent event = fromJson(message.value());
            if (StringUtils.isBlank(event.getTenantKey())) {
                log.info("Event ignored due to tenantKey is empty {}", event.getEventType());
                return;
            }

            tenantContextHolder.getPrivilegedContext().execute(TenantContextUtils.buildTenant(event.getTenantKey()), () -> {
                try (var ctx = lepManagementService.beginThreadContext()) {
                    String newRid = MdcUtils.getRid()
                        + ":" + StringUtils.defaultIfBlank(event.getUserLogin(), "")
                        + ":" + StringUtils.defaultIfBlank(event.getTenantKey(), "");
                    MdcUtils.putRid(newRid);
                    systemQueueProcessorService.handleSystemEvent(event);
                } finally {
                    MdcUtils.removeRid();
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }


    }

    private SystemEvent fromJson(String value) {
        try {
            return objectMapper.readValue(value, SystemEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
