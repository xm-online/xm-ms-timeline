package com.icthh.xm.ms.timeline.repository.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.icthh.xm.commons.logging.util.MDCUtil;
import com.icthh.xm.ms.timeline.config.Constants;
import com.icthh.xm.ms.timeline.domain.SystemEvent;
import com.icthh.xm.ms.timeline.service.tenant.KafkaService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantCommandConsumer {

    private final KafkaService kafkaService;

    /**
     * Consume tenant command event message.
     * @param message the tenant command event message
     */
    @Retryable(maxAttemptsExpression = "${application.retry.max-attempts}",
        backoff = @Backoff(delayExpression = "${application.retry.delay}",
            multiplierExpression = "${application.retry.multiplier}"))
    public void consumeEvent(ConsumerRecord<String, String> message) {
        MDCUtil.put();
        try {
            log.info("Input command {}", message);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            try {
                SystemEvent event = mapper.readValue(message.value(), SystemEvent.class);
                String tenant = event.getData().get(Constants.EVENT_TENANT);
                String command = event.getEventType();
                switch (command.toUpperCase()) {
                    case Constants.CREATE_COMMAND:
                        kafkaService.createKafkaConsumer(tenant);
                        break;
                    case Constants.DELETE_COMMAND:
                        kafkaService.deleteKafkaConsumer(tenant);
                        break;
                    default:
                        break;
                }

            } catch (IOException e) {
                log.error("Kafka message has incorrect format ", e);
            }

        } finally {
            MDCUtil.remove();
        }
    }
}
