package com.icthh.xm.ms.timeline.config.tenant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.icthh.xm.commons.config.client.repository.TenantConfigRepository;
import com.icthh.xm.commons.migration.db.liquibase.LiquibaseRunner;
import com.icthh.xm.commons.migration.db.tenant.DropSchemaResolver;
import com.icthh.xm.commons.migration.db.tenant.provisioner.TenantDatabaseProvisioner;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenantendpoint.TenantManager;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantAbilityCheckerProvisioner;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantConfigProvisioner;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantListProvisioner;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantProvisioner;
import com.icthh.xm.commons.topic.domain.TopicConfig;
import com.icthh.xm.commons.topic.domain.TopicConsumersSpec;
import com.icthh.xm.ms.timeline.config.ApplicationProperties;
import com.icthh.xm.ms.timeline.config.Constants;
import com.icthh.xm.ms.timeline.service.tenant.provisioner.TenantKafkaProvisioner;
import com.icthh.xm.ms.timeline.service.tenant.provisioner.TenantLoggerStorageProvisioner;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

import static com.icthh.xm.commons.config.domain.Configuration.of;
import static com.icthh.xm.ms.timeline.config.Constants.LOGGER_IMPL;
import static com.icthh.xm.ms.timeline.config.Constants.RDBMS_IMPL;
import static com.icthh.xm.ms.timeline.config.Constants.TOPIC_CONFIG_EVENT_FORMAT;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TenantManagerConfiguration {

    private final TenantContextHolder tenantContextHolder;
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @Bean
    public TenantManager tenantManager(TenantAbilityCheckerProvisioner abilityCheckerProvisioner,
                                       @Qualifier("storageTenantProvisioner") TenantProvisioner storageProvisioner,
                                       TenantConfigProvisioner configProvisioner,
                                       TenantListProvisioner tenantListProvisioner,
                                       TenantKafkaProvisioner kafkaProvisioner) {

        TenantManager manager = TenantManager.builder()
                                             .service(abilityCheckerProvisioner)
                                             .service(tenantListProvisioner)
                                             .service(storageProvisioner)
                                             .service(kafkaProvisioner)
                                             .service(configProvisioner)
                                             .build();
        log.info("Configured tenant manager: {}", manager);
        return manager;
    }

    @SneakyThrows
    @Bean
    public TenantConfigProvisioner tenantConfigProvisioner(TenantConfigRepository tenantConfigRepository,
                                                           ApplicationProperties applicationProperties) {
        TenantConfigProvisioner provisioner = TenantConfigProvisioner
            .builder()
            .tenantConfigRepository(tenantConfigRepository)
            .configuration(of().path(applicationProperties.getTenantPropertiesPathPattern())
                               .content(readResource(Constants.DEFAULT_CONFIG_PATH))
                               .build())
            .configuration(of().path(applicationProperties.getDomainEventTopicsPathPattern())
                                .content(getTopicConsumerSpec())
                                .build())
            .build();

        log.info("Configured tenant config provisioner: {}", provisioner);
        return provisioner;
    }

    @Bean("storageTenantProvisioner")
    @ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = RDBMS_IMPL)
    public TenantProvisioner rdbmsTenantProvisioner(DataSource dataSource,
                                                    LiquibaseProperties liquibaseProperties,
                                                    DropSchemaResolver schemaDropResolver,
                                                    LiquibaseRunner liquibaseRunner) {
        return new TenantDatabaseProvisioner(dataSource, liquibaseProperties, schemaDropResolver, liquibaseRunner);
    }

    @Bean("storageTenantProvisioner")
    @ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = LOGGER_IMPL)
    public TenantProvisioner loggerTenantProvisioner() {
        return new TenantLoggerStorageProvisioner();
    }

    @SneakyThrows
    private String readResource(String location) {
        return IOUtils.toString(new ClassPathResource(location).getInputStream(), UTF_8);
    }

    @SneakyThrows
    private String getTopicConsumerSpec() {
        String json = readResource(Constants.TOPIC_CONFIG_PATH);
        TopicConsumersSpec topicConsumersSpec = mapper.readValue(json, TopicConsumersSpec.class);
        Objects.requireNonNull(topicConsumersSpec, String.format("Unable to obtain mapping metadata for %s!", TopicConsumersSpec.class));

        List<TopicConfig> topics = topicConsumersSpec.getTopics();
        Objects.requireNonNull(topics, String.format("Unable to obtain mapping metadata for %s!", TopicConfig.class));

        String tenantKey = tenantContextHolder.getTenantKey();

        topics.forEach(topic -> {
            String topicName = String.format(TOPIC_CONFIG_EVENT_FORMAT, tenantKey.toLowerCase(), topic.getKey());
            topic.setTopicName(topicName);
        });
        return mapper.writeValueAsString(topicConsumersSpec);
    }
}
