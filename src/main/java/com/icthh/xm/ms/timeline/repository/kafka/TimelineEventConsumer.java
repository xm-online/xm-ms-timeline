package com.icthh.xm.ms.timeline.repository.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.icthh.xm.commons.logging.aop.IgnoreLogginAspect;
import com.icthh.xm.commons.logging.util.MdcUtils;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.ms.timeline.config.ApplicationProperties;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.service.TenantPropertiesService;
import com.icthh.xm.ms.timeline.service.TimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
@IgnoreLogginAspect
public class TimelineEventConsumer {
    private final TimelineService timelineService;
    private final TenantPropertiesService tenantPropertiesService;
    private final TenantContextHolder tenantContextHolder;
    private final ApplicationProperties applicationProperties;

    /**
     * Consume timeline event message.
     *
     * @param message the timeline event message
     */
    @Retryable(maxAttemptsExpression = "${application.retry.max-attempts}",
        backoff = @Backoff(delayExpression = "${application.retry.delay}",
            multiplierExpression = "${application.retry.multiplier}"))
    public void consumeEvent(ConsumerRecord<String, String> message) {
        String rid = MdcUtils.generateRid();
        MdcUtils.putRid(rid);
        try {
            log.info("Consume event from topic [{}]", message.topic());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            try {
                XmTimeline xmTimeline = mapper.readValue(message.value(), XmTimeline.class);
                if (StringUtils.isBlank(xmTimeline.getTenant())) {
                    xmTimeline.setTenant(message.topic());
                }
                MdcUtils.putRid(rid + ":" + xmTimeline.getTenant());
                tenantContextHolder.getPrivilegedContext()
                    .execute(TenantContextUtils.buildTenant(xmTimeline.getTenant()),
                        buildExclusionAwareTimelineAdder(), xmTimeline);
            } catch (IOException e) {
                log.error("Timeline message has incorrect format: '{}'", message.value(), e);
            }
        } finally {
            MdcUtils.clear();
        }
    }

    private Consumer<XmTimeline> buildExclusionAwareTimelineAdder() {
        return (xmTimeline) -> {
            List<String> excludeMethods = tenantPropertiesService.getTenantProps().getFilter().getExcludeMethod();

            if (applicationProperties.getGeneralFilters() != null && applicationProperties.getGeneralFilters().getIncludeEntityTypeRegex() != null) {
                String regex = applicationProperties.getGeneralFilters().getIncludeEntityTypeRegex();

                if (!xmTimeline.getEntityTypeKey().matches(regex)) {
                    log.debug(
                        "Message with [rid={},operationUrl={},msName={},httpStatus={}] was excluded by entity type key: [{}]",
                        xmTimeline.getRid(),
                        xmTimeline.getOperationUrl(),
                        xmTimeline.getMsName(),
                        xmTimeline.getHttpStatusCode(),
                        xmTimeline.getEntityTypeKey());
                    return;
                }
            }

            if (CollectionUtils.isNotEmpty(excludeMethods) && excludeMethods.contains(xmTimeline.getHttpMethod())) {
                log.debug(
                    "Message with [rid={},operationUrl={},msName={},httpStatus={}] was excluded by http method: [{}]",
                    xmTimeline.getRid(),
                    xmTimeline.getOperationUrl(),
                    xmTimeline.getMsName(),
                    xmTimeline.getHttpStatusCode(),
                    xmTimeline.getHttpMethod());
                return;
            }
            timelineService.insertTimelines(xmTimeline);
        };
    }
}
