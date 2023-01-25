package com.icthh.xm.ms.timeline.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icthh.xm.commons.logging.util.MdcUtils;
import com.icthh.xm.commons.messaging.event.system.SystemEvent;
import com.icthh.xm.ms.timeline.service.SystemQueueProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            systemQueueProcessorService.handleSystemEvent(event);
        } finally {
            MdcUtils.removeRid();
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