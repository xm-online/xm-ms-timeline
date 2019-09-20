package com.icthh.xm.ms.timeline.repository.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.icthh.xm.commons.logging.util.MdcUtils;
import com.icthh.xm.commons.messaging.event.system.SystemEvent;
import com.icthh.xm.commons.messaging.event.system.SystemEventType;
import com.icthh.xm.ms.timeline.config.Constants;
import com.icthh.xm.ms.timeline.service.kafka.TimelineEventConsumerHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class SystemTopicConsumer {

    private final TimelineEventConsumerHolder timelineConsumerHolder;

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
            log.info("Consume event from topic [{}]", message.topic());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            try {
                SystemEvent event = mapper.readValue(message.value(), SystemEvent.class);

                log.info("Process event from topic [{}], type='{}', source='{}', event_id ='{}'",
                    message.topic(), event.getEventType(), event.getMessageSource(), event.getEventId());

                String tenant = Objects.toString(event.getDataMap().get(Constants.EVENT_TENANT), null);
                String command = event.getEventType();
                switch (command.toUpperCase()) {
                    case SystemEventType.CREATE_COMMAND:
                        timelineConsumerHolder.createConsumer(tenant);
                        break;
                    case SystemEventType.DELETE_COMMAND:
                        timelineConsumerHolder.deleteConsumer(tenant);
                        break;
                    default:
                        log.info("Event ignored with type='{}', source='{}', event_id='{}'",
                            event.getEventType(), event.getMessageSource(), event.getEventId());
                        break;
                }

            } catch (IOException e) {
                log.error("System topic message has incorrect format: '{}' ", message.value(), e);
            }

        } finally {
            MdcUtils.removeRid();
        }
    }
}
