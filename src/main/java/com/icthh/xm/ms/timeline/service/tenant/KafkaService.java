package com.icthh.xm.ms.timeline.service.tenant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.icthh.xm.commons.errors.exception.BusinessException;
import com.icthh.xm.commons.logging.aop.IgnoreLogginAspect;
import com.icthh.xm.commons.logging.util.MDCUtil;
import com.icthh.xm.ms.timeline.config.ApplicationProperties;
import com.icthh.xm.ms.timeline.config.Constants;
import com.icthh.xm.ms.timeline.config.tenant.TenantContext;
import com.icthh.xm.ms.timeline.domain.SystemEvent;
import com.icthh.xm.ms.timeline.repository.kafka.TimelineEventConsumer;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
@Service
@IgnoreLogginAspect
public class KafkaService {

    private final ConsumerFactory<String, String> consumerFactory;
    private final TimelineEventConsumer consumer;
    private final ApplicationProperties properties;
    private final KafkaTemplate<String, String> template;

    @Value("${spring.application.name}")
    private String appName;

    private Map<String, ConcurrentMessageListenerContainer<String, String>> consumers = new HashMap<>();

    /**
     * Create kafka topic.
     * @param tenant the topic name
     */
    public void createKafkaTopic(String tenant) {
        ZkClient zkClient = null;
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            log.info("START - SETUP:CreateTenant:kafka topic tenantKey: {}", tenant);
            zkClient = new ZkClient(properties.getZookeeper().getHost(),
                properties.getZookeeper().getSessionTimeout(),
                properties.getZookeeper().getConnectionTimeout(),
                ZKStringSerializer$.MODULE$);


            ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(properties.getZookeeper().getHost()), false);

            if (!AdminUtils.topicExists(zkUtils, tenant)) {
                AdminUtils.createTopic(zkUtils, tenant,
                    properties.getZookeeper().getPartitions(),
                    properties.getZookeeper().getReplication(),
                    new Properties(), RackAwareMode.Enforced$.MODULE$);
            }
            log.info("STOP  - SETUP:CreateTenant:kafka topic tenantKey: {}, result: OK, time = {} ms",
                tenant, stopWatch.getTime());
        } catch (Exception e) {
            log.error("STOP  - SETUP:CreateTenant:kafka topic tenantKey: {}, result: FAIL, error: {}, time = {} ms",
                tenant, e.getMessage(), stopWatch.getTime());
            throw e;
        } finally {
            if (zkClient != null) {
                zkClient.close();
            }
        }
    }

    /**
     * Delete kafka topic.
     * @param tenant the kafka topic
     */
    public void deleteKafkaTopic(String tenant) {
        ZkClient zkClient = null;
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            log.info("START - SETUP:DeleteTenant:kafka topic tenantKey: {}", tenant);
            zkClient = new ZkClient(properties.getZookeeper().getHost(),
                properties.getZookeeper().getSessionTimeout(),
                properties.getZookeeper().getConnectionTimeout(),
                ZKStringSerializer$.MODULE$);

            ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(properties.getZookeeper().getHost()), false);

            if (AdminUtils.topicExists(zkUtils, tenant)) {
                AdminUtils.deleteTopic(zkUtils, tenant);
            }
            log.info("STOP  - SETUP:DeleteTenant:kafka topic tenantKey: {}, result: OK, time = {} ms",
                tenant, stopWatch.getTime());
        } catch (Exception e) {
            log.error("STOP  - SETUP:DeleteTenant:kafka topic tenantKey: {}, result: FAIL, error: {}, time = {} ms",
                tenant, e.getMessage(), stopWatch.getTime(), e);
        } finally {
            if (zkClient != null) {
                zkClient.close();
            }
        }
    }

    /**
     * Create topic consumer.
     * @param tenant the kafka topic
     */
    public void createKafkaConsumer(String tenant) {
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            log.info("START - SETUP:CreateTenant:kafka consumer tenantKey: {}", tenant);
            ConcurrentMessageListenerContainer<String, String> container = consumers.get(tenant);
            if (container != null) {
                if (!container.isRunning()) {
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
            log.info("STOP  - SETUP:CreateTenant:kafka consumer tenantKey: {}, result: OK, time = {} ms",
                tenant, stopWatch.getTime());
        } catch (Exception e) {
            log.error("STOP  - SETUP:CreateTenant:kafka consumer tenantKey: {}, result: FAIL, error: {}, time = {} ms",
                tenant, e.getMessage(), stopWatch.getTime(), e);
        }
    }

    /**
     * Delete topic consumer.
     * @param tenant the kafka topic
     */
    public void deleteKafkaConsumer(String tenant) {
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            log.info("START - SETUP:DeleteTenant:kafka consumer tenantKey: {}", tenant);
            if (consumers.get(tenant) != null) {
                consumers.get(tenant).stop();
                consumers.remove(tenant);
            }
            log.info("STOP  - SETUP:DeleteTenant:kafka consumer tenantKey: {}, result: OK, time = {} ms",
                tenant, stopWatch.getTime());
        } catch (Exception e) {
            log.error("STOP  - SETUP:DeleteTenant:kafka consumer tenantKey: {}, result: FAIL, error: {}, time = {} ms",
                tenant, e.getMessage(), stopWatch.getTime(), e);
        }
    }

    /**
     * Send 'tenant management' command to system topic.
     * @param tenant the tenant to manage
     * @param command the command (e.g. CREATE, DELETE, ...)
     */
    public void sendCommand(String tenant, String command) {
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            log.info("START - SETUP:ManageTenant:kafka send command tenantKey: {}, command: {}",
                tenant, command);
            template.send(properties.getKafkaSystemTopic(), tenant, createSystemEvent(tenant, command));
            log.info("STOP  - SETUP:ManageTenant:kafka send command tenantKey: {}, command: {},"
                    + " result: OK, time = {} ms",
                tenant, command, stopWatch.getTime());
        } catch (Exception e) {
            log.error("STOP  - SETUP:ManageTenant:kafka send command tenantKey: {}, command: {},"
                    + " result: FAIL, error: {}, time = {} ms",
                tenant, command, e.getMessage(), stopWatch.getTime());
            throw e;
        }
    }

    private String createSystemEvent(String tenant, String command) {
        SystemEvent event = new SystemEvent();
        event.setEventId(MDCUtil.getRid());
        event.setEventType(command);
        event.setTenantInfo(TenantContext.getCurrent());
        event.setMessageSource(appName);
        event.getData().put(Constants.EVENT_TENANT, tenant);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("System Event mapping error", e);
            throw new BusinessException("Event mapping error", e.getMessage());
        }

    }
}
