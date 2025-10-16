package com.icthh.xm.ms.timeline.repository.kafka;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.icthh.xm.commons.lep.api.LepManagementService;
import com.icthh.xm.commons.logging.aop.IgnoreLogginAspect;
import com.icthh.xm.commons.logging.util.MdcUtils;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.service.TenantPropertiesService;
import com.icthh.xm.ms.timeline.service.TimelineService;
import com.icthh.xm.ms.timeline.service.dto.TimelineEvent;
import com.icthh.xm.ms.timeline.service.mapper.XmTimelineMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
@IgnoreLogginAspect
public class TimelineEventConsumer {
    private final TimelineService timelineService;
    private final XmTimelineMapper xmTimelineMapper;
    private final TenantPropertiesService tenantPropertiesService;
    private final TenantContextHolder tenantContextHolder;
    private final LepManagementService lepManagementService;
    private final ObjectMapper mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    /**
     * Consume timeline event message.
     *
     * @param message the timeline event message
     */
    @Retryable (maxAttemptsExpression = "${application.retry.max-attempts}",
        backoff = @Backoff(delayExpression = "${application.retry.delay}",
            multiplierExpression = "${application.retry.multiplier}"))
    public void consumeEvent(ConsumerRecord<String, String> message) {
        String rid = MdcUtils.generateRid();
        MdcUtils.putRid(rid);
        try {
            log.info("Consume event from topic [{}]", message.topic());
            try {
                TimelineEvent timelineEvent = mapper.readValue(message.value(), TimelineEvent.class);
                XmTimeline xmTimeline = xmTimelineMapper.timelineEventToXmTimeline(timelineEvent);
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
            try (var context = lepManagementService.beginThreadContext()) {
                timelineService.insertTimelines(xmTimeline);
            }
        };
    }
}
