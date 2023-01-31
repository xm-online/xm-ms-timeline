package com.icthh.xm.ms.timeline.listeners;

import static com.icthh.xm.ms.timeline.config.Constants.CASSANDRA_IMPL;

import com.icthh.xm.commons.config.client.repository.TenantListRepository;
import com.icthh.xm.commons.logging.util.MdcUtils;
import com.icthh.xm.commons.permission.inspector.PrivilegeInspector;
import com.icthh.xm.ms.timeline.config.ApplicationProperties;
import com.icthh.xm.ms.timeline.repository.kafka.SystemTopicConsumer;
import com.icthh.xm.ms.timeline.service.kafka.SystemQueueConsumer;
import com.icthh.xm.ms.timeline.service.kafka.TimelineEventConsumerHolder;
import com.icthh.xm.ms.timeline.service.tenant.provisioner.TenantCassandraStorageProvisioner;
import io.github.jhipster.config.JHipsterConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    private final ApplicationProperties properties;
    private final TimelineEventConsumerHolder timelineConsumerHolder;
    private final SystemTopicConsumer commandConsumer;
    private final Environment env;
    private final KafkaProperties kafkaProperties;
    private final TenantListRepository tenantListRepository;
    private final PrivilegeInspector privilegeInspector;
    private final TenantCassandraStorageProvisioner tenantCassandraStorageProvisioner;
    private final SystemQueueConsumer systemQueueConsumer;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        if (!env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_TEST))) {
            createKafkaSystemQueueConsumers();
            createKafkaConsumers();
            if (StringUtils.equalsIgnoreCase(properties.getTimelineServiceImpl(), CASSANDRA_IMPL)) {
                migrateCassandra();
            }
            privilegeInspector.readPrivileges(MdcUtils.getRid());
        } else {
            log.warn("WARNING! Privileges inspection is disabled by "
                + "configuration parameter 'application.kafka-enabled'");
        }
    }

    private void migrateCassandra() {
        tenantListRepository.getTenants().forEach(tenantName -> {
            log.info("Start cassandra migration for tenant {}", tenantName);
            try {
                tenantCassandraStorageProvisioner.createCassandraKeyspaceIfNotExist(tenantName);
                tenantCassandraStorageProvisioner.migrateCassandra(tenantName);
            } catch (Exception e) {
                log.error("Cassandra migration failed for tenant {}, error: {}",
                    tenantName, e.getMessage(), e);
            }
        });
    }

    private void createKafkaConsumers() {
        Set<String> tenants = tenantListRepository.getTenants();

        log.info("Create timeline consumers for [{}] active tenants", tenants.size());
        tenants.stream()
               .map(String::toUpperCase)
               .forEach(timelineConsumerHolder::createConsumer);

        createCommandConsumer(properties.getKafkaSystemTopic());
    }

    private void createCommandConsumer(String name) {
        log.info("Creating kafka command consumer for topic {}", name);
        ContainerProperties containerProps = new ContainerProperties(name);

        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        ConsumerFactory<String, String> factory = new DefaultKafkaConsumerFactory<>(props);

        ConcurrentMessageListenerContainer<String, String> container =
            new ConcurrentMessageListenerContainer<>(factory, containerProps);
        container.setupMessageListener((MessageListener<String, String>) commandConsumer::consumeEvent);
        container.start();
        log.info("Successfully created kafka command consumer for topic {}", name);
    }

    private void createKafkaSystemQueueConsumers() {
        createSystemConsumer(properties.getKafkaSystemQueue(), systemQueueConsumer::consumeEvent);
    }

    private void createSystemConsumer(String name, MessageListener<String, String> consumeEvent) {
        log.info("Creating kafka consumer for topic {}", name);
        ContainerProperties containerProps = new ContainerProperties(name);

        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, properties.getKafkaMetadataMaxAge());
        ConsumerFactory<String, String> factory = new DefaultKafkaConsumerFactory<>(props);

        ConcurrentMessageListenerContainer<String, String> container =
                new ConcurrentMessageListenerContainer<>(factory, containerProps);
        container.setupMessageListener(consumeEvent);
        container.start();
        log.info("Successfully created kafka consumer for topic {}", name);
    }
}
