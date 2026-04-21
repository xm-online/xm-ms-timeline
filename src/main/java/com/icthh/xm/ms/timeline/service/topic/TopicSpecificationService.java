package com.icthh.xm.ms.timeline.service.topic;

import com.icthh.xm.commons.tenant.YamlMapperUtils;
import tools.jackson.databind.ObjectMapper;
import com.icthh.xm.commons.topic.domain.DynamicConsumer;
import com.icthh.xm.commons.topic.domain.TopicConfig;
import com.icthh.xm.commons.topic.domain.TopicConsumersSpec;
import com.icthh.xm.commons.topic.service.DynamicConsumerConfiguration;
import com.icthh.xm.ms.timeline.service.kafka.DomainEventMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TopicSpecificationService implements DynamicConsumerConfiguration {

    private final Map<String, List<DynamicConsumer>> dynamicConsumersByTenant = new ConcurrentHashMap<>();
    private final ObjectMapper ymlMapper = YamlMapperUtils.yamlDefaultMapper();
    private final TopicMessageHandlerFactory topicMessageHandlerFactory;

    public void processTopicSpecifications(String tenantKey, String config) {
        if (StringUtils.isBlank(config)) {
            dynamicConsumersByTenant.remove(tenantKey);
            return;
        }

        TopicConsumersSpec topicSpec = readTopicSpec(config);
        List<DynamicConsumer> dynamicConsumerList = topicSpec.getTopics().stream()
            .map(this::buildDynamicConsumer)
            .toList();

        dynamicConsumersByTenant.put(tenantKey, dynamicConsumerList);
    }

    @Override
    public List<DynamicConsumer> getDynamicConsumers(String tenantKey) {
        List<DynamicConsumer> dynamicConsumers = dynamicConsumersByTenant.get(tenantKey);
        if (dynamicConsumers == null) {
            return List.of();
        }
        return dynamicConsumers;
    }

    @SneakyThrows
    private TopicConsumersSpec readTopicSpec(String config) {
        return ymlMapper.readValue(config, TopicConsumersSpec.class);
    }

    private DynamicConsumer buildDynamicConsumer(TopicConfig topicConfig) {
        DomainEventMessageHandler topicMessageHandler = topicMessageHandlerFactory.createTopicMessageHandler();
        DynamicConsumer dynamicConsumer = new DynamicConsumer();
        dynamicConsumer.setConfig(topicConfig);
        dynamicConsumer.setMessageHandler(topicMessageHandler);
        return dynamicConsumer;
    }
}
