package com.icthh.xm.ms.timeline.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icthh.xm.commons.config.client.repository.TenantListRepository;
import com.icthh.xm.commons.logging.trace.SleuthWrapper;
import com.icthh.xm.commons.security.XmAuthenticationContextHolder;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.topic.message.LepMessageHandler;
import com.icthh.xm.commons.topic.message.MessageHandler;
import com.icthh.xm.commons.topic.message.MessageService;
import com.icthh.xm.commons.topic.service.DynamicConsumerConfiguration;
import com.icthh.xm.commons.topic.service.DynamicConsumerConfigurationService;
import com.icthh.xm.commons.topic.service.TopicConfigurationService;
import com.icthh.xm.commons.topic.service.TopicManagerService;
import com.icthh.xm.lep.api.LepManager;
import com.icthh.xm.ms.timeline.service.DomainEventService;
import com.icthh.xm.ms.timeline.service.kafka.DomainEventMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class TopicManagerConfiguration {

    private final ObjectMapper objectMapper;
    private final DomainEventService domainEventService;
    private final MessageService messageService;
    private final TenantContextHolder tenantContextHolder;
    private final XmAuthenticationContextHolder authContextHolder;
    private final LepManager lepManager;

    @Bean
    public TopicConfigurationService topicConfigurationService(@Value("${spring.application.name}") String appName,
                                                               KafkaProperties kafkaProperties,
                                                               KafkaTemplate<String, String> kafkaTemplate,
                                                               SleuthWrapper sleuthWrapper,
                                                               TenantListRepository tenantListRepository) {
        List<DynamicConsumerConfiguration> dynamicConsumerConfigurations = new ArrayList<>();

        TopicConfigurationService topicConfigurationService = new TopicConfigurationService(appName,
            dynamicConsumerConfigurationService(kafkaProperties, kafkaTemplate, sleuthWrapper, tenantListRepository, dynamicConsumerConfigurations),
            consumerMessageHandler());

        dynamicConsumerConfigurations.add(topicConfigurationService);

        return topicConfigurationService;
    }

    @Bean
    public DynamicConsumerConfigurationService dynamicConsumerConfigurationService(KafkaProperties kafkaProperties,
                                                                                   KafkaTemplate<String, String> kafkaTemplate,
                                                                                   SleuthWrapper sleuthWrapper,
                                                                                   TenantListRepository tenantListRepository,
                                                                                   List<DynamicConsumerConfiguration> dynamicConsumerConfigurations) {
        return new DynamicConsumerConfigurationService(dynamicConsumerConfigurations,
            topicManagerService(kafkaProperties, kafkaTemplate, sleuthWrapper), tenantListRepository);
    }

    @Bean
    public TopicManagerService topicManagerService(KafkaProperties kafkaProperties,
                                                   KafkaTemplate<String, String> kafkaTemplate,
                                                   SleuthWrapper sleuthWrapper) {
        return new TopicManagerService(kafkaProperties, kafkaTemplate, sleuthWrapper);
    }

    @Bean
    public MessageHandler consumerMessageHandler() {
        return new DomainEventMessageHandler(objectMapper, domainEventService, tenantContextHolder, authContextHolder, lepManager);
    }

    @Bean
    public MessageHandler lepMessageHandler() {
        return new LepMessageHandler(messageService, tenantContextHolder, authContextHolder, lepManager);
    }
}
