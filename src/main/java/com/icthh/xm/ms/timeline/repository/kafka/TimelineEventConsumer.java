package com.icthh.xm.ms.timeline.repository.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.icthh.xm.commons.logging.aop.IgnoreLogginAspect;
import com.icthh.xm.commons.logging.util.MdcUtils;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.service.TenantPropertiesService;
import com.icthh.xm.ms.timeline.service.TimelineService;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@IgnoreLogginAspect
public class TimelineEventConsumer {
    private final TimelineService timelineService;
    private final TenantPropertiesService tenantPropertiesService;
    private final TenantContextHolder tenantContextHolder;

    /**
     * Consume timeline event message.
     *
     * @param message the timeline event message
     */
    @Retryable(maxAttemptsExpression = "${application.retry.max-attempts}",
        backoff = @Backoff(delayExpression = "${application.retry.delay}",
            multiplierExpression = "${application.retry.multiplier}"))
    public void consumeEvent(ConsumerRecord<String, String> message) {
        MdcUtils.putRid();
        try {
            log.info("Consume event from topic [{}]", message.topic());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            try {
                XmTimeline xmTimeline = mapper.readValue(message.value(), XmTimeline.class);
                TenantContextUtils.setTenant(tenantContextHolder, xmTimeline.getTenant());
                if (CollectionUtils.isNotEmpty(tenantPropertiesService.getTenantProps().getFilter().getExcludeMethod())
                    && tenantPropertiesService.getTenantProps().getFilter().getExcludeMethod()
                    .contains(xmTimeline.getHttpMethod())) {
                    log.debug("Message '{}' was excluded by http method", xmTimeline);
                    return;
                }
                if (StringUtils.isBlank(xmTimeline.getTenant())) {
                    xmTimeline.setTenant(message.topic());
                }
                timelineService.insertTimelines(xmTimeline);
            } catch (IOException e) {
                log.error("Timeline message has incorrect format: '{}'", message.value(), e);
            }
        } finally {
            tenantContextHolder.getPrivilegedContext().destroyCurrentContext();
            MdcUtils.removeRid();
        }
    }
}
