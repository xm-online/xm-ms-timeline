package com.icthh.xm.ms.timeline.config.topic;

import com.icthh.xm.commons.config.client.api.RefreshableConfiguration;
import com.icthh.xm.commons.topic.service.DynamicConsumerConfigurationService;
import com.icthh.xm.ms.timeline.config.ApplicationProperties;
import com.icthh.xm.ms.timeline.service.topic.TopicSpecificationService;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

@Service
public class TopicSpecificationConfiguration implements RefreshableConfiguration {

    private static final String TENANT_NAME = "tenantName";

    private final String pathPattern;
    private final TopicSpecificationService topicSpecificationService;
    private final DynamicConsumerConfigurationService dynamicConsumerConfigurationService;
    private final AntPathMatcher matcher = new AntPathMatcher();

    public TopicSpecificationConfiguration(ApplicationProperties applicationProperties,
                                           TopicSpecificationService topicSpecificationService,
                                           DynamicConsumerConfigurationService dynamicConsumerConfigurationService) {
        this.pathPattern = applicationProperties.getDomainEventTopicsPathPattern();
        this.topicSpecificationService = topicSpecificationService;
        this.dynamicConsumerConfigurationService = dynamicConsumerConfigurationService;
    }

    @Override
    public void onRefresh(String updatedKey, String config) {
        String tenantName = extractTenantName(updatedKey);
        topicSpecificationService.processTopicSpecifications(tenantName, config);
        dynamicConsumerConfigurationService.refreshDynamicConsumers(tenantName);
    }

    @Override
    public boolean isListeningConfiguration(String updatedKey) {
        return matcher.match(pathPattern, updatedKey);
    }

    private String extractTenantName(String updatedKey) {
        return matcher.extractUriTemplateVariables(pathPattern, updatedKey).get(TENANT_NAME);
    }
}
