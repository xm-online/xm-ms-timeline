package com.icthh.xm.ms.timeline.listeners;

import static com.icthh.xm.ms.timeline.config.Constants.CASSANDRA_IMPL;

import com.builtamont.cassandra.migration.CassandraMigration;
import com.builtamont.cassandra.migration.api.configuration.ClusterConfiguration;
import com.builtamont.cassandra.migration.api.configuration.KeyspaceConfiguration;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.icthh.xm.commons.config.client.repository.TenantListRepository;
import com.icthh.xm.commons.logging.util.MdcUtils;
import com.icthh.xm.commons.permission.inspector.PrivilegeInspector;
import com.icthh.xm.ms.timeline.config.ApplicationProperties;
import com.icthh.xm.ms.timeline.repository.kafka.SystemTopicConsumer;
import com.icthh.xm.ms.timeline.repository.kafka.TimelineEventConsumer;
import io.github.jhipster.config.JHipsterConstants;

import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    private final ApplicationProperties properties;
    private final ConsumerFactory<String, String> consumerFactory;
    private final TimelineEventConsumer timelineConsumer;
    private final SystemTopicConsumer commandConsumer;
    private final CassandraProperties cassandraProperties;
    private final Environment env;
    private final KafkaProperties kafkaProperties;
    private final TenantListRepository tenantListRepository;
    private final PrivilegeInspector privilegeInspector;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!env.acceptsProfiles(JHipsterConstants.SPRING_PROFILE_TEST)) {
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
        ClusterConfiguration clusterConfiguration = new ClusterConfiguration();
        String[] contactPoints = cassandraProperties.getContactPoints()
            .toArray(new String[cassandraProperties.getContactPoints().size()]);
        clusterConfiguration.setContactpoints(contactPoints);
        CassandraMigration cm = new CassandraMigration();

        try (Cluster cluster = Cluster.builder().addContactPoints(contactPoints).build();
             Session session = cluster.connect()) {

            tenantListRepository.getTenants().forEach(tenantName -> {
                log.info("Start cassandra migration for tenant {}", tenantName);
                try {
                    session.execute(String.format(properties.getCassandra().getKeyspaceCreateCql(), tenantName));
                    KeyspaceConfiguration keyspaceConfiguration = new KeyspaceConfiguration();
                    keyspaceConfiguration.setName(tenantName.toLowerCase());
                    keyspaceConfiguration.setClusterConfig(clusterConfiguration);
                    cm.setLocations(new String[]{properties.getCassandra().getMigrationFolder()});
                    cm.setKeyspaceConfig(keyspaceConfiguration);
                    cm.migrate();
                    log.info("Stop cassandra migration for tenant {}", tenantName);
                } catch (Exception e) {
                    log.error("Cassandra migration failed for tenant {}, error: {}",
                        tenantName, e.getMessage(), e);
                }
            });
        }
    }

    private void createKafkaConsumers() {
        tenantListRepository.getTenants().stream().map(String::toUpperCase).forEach(this::createConsumer);
        createCommandConsumer(properties.getKafkaSystemTopic());
    }

    private void createConsumer(String name) {
        log.info("Creating kafka consumer for tenant {}", name);
        try {
            ContainerProperties containerProps = new ContainerProperties(name);
            ConcurrentMessageListenerContainer<String, String> container =
                new ConcurrentMessageListenerContainer<>(consumerFactory, containerProps);
            container.setupMessageListener((MessageListener<String, String>) timelineConsumer::consumeEvent);
            container.start();
            log.info("Successfully created kafka consumer for tenant {}", name);
        } catch (Exception e) {
            log.error("Kafka consumer creation failed for tenant {}, error: {}", name, e.getMessage(), e);
        }
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
}
