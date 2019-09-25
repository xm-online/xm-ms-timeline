package com.icthh.xm.ms.timeline.service.tenant.provisioner;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Maps;
import com.icthh.xm.commons.exceptions.BusinessException;
import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.commons.logging.util.MdcUtils;
import com.icthh.xm.commons.messaging.event.system.SystemEvent;
import com.icthh.xm.commons.security.XmAuthenticationContextHolder;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantProvisioner;
import com.icthh.xm.ms.timeline.config.ApplicationProperties;
import com.icthh.xm.ms.timeline.config.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsOptions;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.DescribeConsumerGroupsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.MemberAssignment;
import org.apache.kafka.clients.admin.MemberDescription;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.TopicExistsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Service
public class TenantKafkaProvisioner implements TenantProvisioner {

    private static final String TIMELINE_CONSUMER_GROUP = "timeline";
    private static final Long WAIT_DISCONNECT_MS = 1000L;

    private final ApplicationProperties properties;
    private final KafkaTemplate<String, String> template;
    private final TenantContextHolder tenantContextHolder;
    private final XmAuthenticationContextHolder authContextHolder;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Override
    public void createTenant(final Tenant tenant) {
        String formattedTenantKey = formatTenantKey(tenant.getTenantKey());
        createTopic(formattedTenantKey);
        sendCommand(formattedTenantKey, Constants.CREATE_COMMAND);
    }

    @Override
    public void manageTenant(final String tenantKey, final String state) {
        log.info("Nothing to do with Kafka during manage tenant: {}, state = {}", tenantKey, state);
    }

    @Override
    public void deleteTenant(final String tenantKey) {
        String formattedTenantKey = formatTenantKey(tenantKey);
        sendCommand(formattedTenantKey, Constants.DELETE_COMMAND);
        deleteTopic(formattedTenantKey);
    }

    String getAppName() {
        return appName;
    }

    String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    /**
     * Creates a topic in Kafka. If the topic already exists this does nothing.
     *
     * @param topicName - the namespace name to create.
     */
    private void createTopic(final String topicName) {
        StopWatch stopWatch = StopWatch.createStarted();
        try (final AdminClient adminClient = KafkaAdminClient.create(buildDefaultClientConfig())) {
            try {
                int partitions = properties.getZookeeper().getPartitions();
                short replications = properties.getZookeeper().getReplication();
                int timeout = properties.getZookeeper().getConnectionTimeout();

                NewTopic newTopic = new NewTopic(topicName, partitions, replications);
                CreateTopicsResult createTopicsResult = adminClient
                    .createTopics(singleton(newTopic), new CreateTopicsOptions().timeoutMs(timeout));
                // Since the call is Async, Lets wait for it to complete.
                createTopicsResult.values().get(topicName).get();
                log.info("Kafka topic created for tenantKey: {}, time = {} ms", topicName, stopWatch.getTime());
            } catch (InterruptedException | ExecutionException e) {
                if (!(e.getCause() instanceof TopicExistsException)) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Delete kafka topic.
     *
     * @param topicName the kafka topic
     */
    private void deleteTopic(final String topicName) {
        StopWatch stopWatch = StopWatch.createStarted();
        try (final AdminClient adminClient = KafkaAdminClient.create(buildDefaultClientConfig())) {
            try {
                assertTopicDisconnected(topicName, adminClient);

                int timeout = properties.getZookeeper().getConnectionTimeout();

                DeleteTopicsResult createTopicsResult =
                    adminClient.deleteTopics(singleton(topicName), new DeleteTopicsOptions().timeoutMs(timeout));
                // Since the call is Async, Lets wait for it to complete.
                createTopicsResult.values().get(topicName).get();
                log.info("Kafka topic deleted for tenantKey: {}, time = {} ms", topicName, stopWatch.getTime());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    private Map<String, Object> buildDefaultClientConfig() {
        Map<String, Object> defaultClientConfig = Maps.newHashMap();
        defaultClientConfig.put("bootstrap.servers", getKafkaBootstrapServers());
        return defaultClientConfig;
    }

    private void assertTopicDisconnected(String topicName, AdminClient adminClient)
    throws InterruptedException, ExecutionException {
        if (hasConsumers(topicName, adminClient)) {
            log.warn("sleep for {} ms to wait for topic [{}] disconnection", WAIT_DISCONNECT_MS, topicName);
            Thread.sleep(WAIT_DISCONNECT_MS); // just wait while SystemTopicConsumer disconnect from topic
            if (hasConsumers(topicName, adminClient)) {
                throw new BusinessException("can not delete topic " + topicName + " as it has active consumers");
            }
        }
    }

    private boolean hasConsumers(final String topicName, final AdminClient adminClient)
    throws InterruptedException, ExecutionException {
        DescribeConsumerGroupsResult result = adminClient.describeConsumerGroups(singleton(TIMELINE_CONSUMER_GROUP));
        ConsumerGroupDescription timeline = result.all().get().get(TIMELINE_CONSUMER_GROUP);
        return timeline.members()
                       .stream()
                       .map(MemberDescription::assignment)
                       .map(MemberAssignment::topicPartitions)
                       .flatMap(Collection::stream)
                       .map(TopicPartition::topic)
                       .anyMatch(topicName::equals);
    }

    /**
     * Send 'tenant management' command to system topic.
     *
     * @param tenant  the tenant to manage
     * @param command the command (e.g. CREATE, DELETE, ...)
     */
    private void sendCommand(String tenant, String command) {
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            template.send(properties.getKafkaSystemTopic(), tenant, createSystemEvent(tenant, command));
        } finally {
            log.info("Sent message to system topic for tenantKey: {}, command = {}, time = {} ms",
                     tenant, command, stopWatch.getTime());
        }
    }

    private String createSystemEvent(String tenant, String command) {
        SystemEvent event = new SystemEvent();
        event.setEventId(MdcUtils.getRid());
        event.setEventType(command);
        event.setTenantKey(TenantContextUtils.getRequiredTenantKeyValue(tenantContextHolder));
        event.setUserLogin(authContextHolder.getContext().getRequiredLogin());
        event.setMessageSource(getAppName());
        //TODO make SystemEvent.data Map by default, check if used everywhere
        event.setData(singletonMap(Constants.EVENT_TENANT, tenant));
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("System Event mapping error", e);
            throw new BusinessException("Event mapping error", e.getMessage());
        }

    }

    private String formatTenantKey(String tenantKey) {
        return tenantKey.toUpperCase();
    }

}
