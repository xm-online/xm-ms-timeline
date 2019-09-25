package com.icthh.xm.ms.timeline.service.kafka;

import com.icthh.xm.ms.timeline.repository.kafka.TimelineEventConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimelineEventConsumerHolder {

    private final ConsumerFactory<String, String> consumerFactory;
    private final TimelineEventConsumer consumer;

    private Map<String, ConcurrentMessageListenerContainer<String, String>> consumers = new ConcurrentHashMap<>();

    /**
     * Create topic consumer.
     *
     * @param tenant the kafka topic
     */
    public void createConsumer(String tenant) {
        ConcurrentMessageListenerContainer<String, String> container = consumers.get(tenant);
        if (container != null) {
            log.info("Consumer was already created: {}", tenant);
            if (!container.isRunning()) {
                log.warn("Consumer was already created byt not started: {}. trying to start...", tenant);
                container.start();
            }
        } else {
            ContainerProperties containerProps = new ContainerProperties(tenant);
            container = new ConcurrentMessageListenerContainer<>(consumerFactory, containerProps);
            container.setupMessageListener((MessageListener<String, String>) consumer::consumeEvent);
            container.setBeanName(tenant);
            container.start();
            consumers.put(tenant, container);
        }
    }

    /**
     * Delete topic consumer.
     *
     * @param tenant the kafka topic
     */
    public void deleteConsumer(String tenant) {
        if (consumers.get(tenant) != null) {
            consumers.get(tenant).stop();
            consumers.remove(tenant);
        } else {
            log.warn("Consumer not found for deletion: {}", tenant);
        }
    }

}
