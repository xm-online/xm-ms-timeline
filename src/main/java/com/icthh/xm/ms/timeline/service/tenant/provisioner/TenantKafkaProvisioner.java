package com.icthh.xm.ms.timeline.service.tenant.provisioner;

import static java.util.Collections.singletonMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
@Service
public class TenantKafkaProvisioner implements TenantProvisioner {

    private final ApplicationProperties properties;
    private final KafkaTemplate<String, String> template;
    private final TenantContextHolder tenantContextHolder;
    private final XmAuthenticationContextHolder authContextHolder;

    @Value("${spring.application.name}")
    private String appName;

    @Override
    public void createTenant(final Tenant tenant) {
        String formattedTenantKey = formatTenantKey(tenant.getTenantKey());
        createKafkaTopic(formattedTenantKey);
        sendCommand(formattedTenantKey, Constants.CREATE_COMMAND);
    }

    @Override
    public void manageTenant(final String tenantKey, final String state) {
        log.info("Nothing to do with Kafka during manage tenant: {}, state = {}", tenantKey, state);
    }

    @Override
    public void deleteTenant(final String tenantKey) {
        String formattedTenantKey = formatTenantKey(tenantKey);
        deleteKafkaTopic(formattedTenantKey);
        sendCommand(formattedTenantKey, Constants.DELETE_COMMAND);
    }

    /**
     * Create kafka topic.
     *
     * @param tenant the topic name
     */
    //TODO - refactor to use AdminClient
    public void createKafkaTopic(String tenant) {
        ZkClient zkClient = null;
        StopWatch stopWatch = StopWatch.createStarted();
        try {
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
            log.info("Kafka topic created for tenantKey: {}, time = {} ms", tenant, stopWatch.getTime());
        } finally {
            if (zkClient != null) {
                zkClient.close();
            }
        }
    }

    /**
     * Delete kafka topic.
     *
     * @param tenant the kafka topic
     */
    //TODO - refactor to use AdminClient
    public void deleteKafkaTopic(String tenant) {
        ZkClient zkClient = null;
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            zkClient = new ZkClient(properties.getZookeeper().getHost(),
                                    properties.getZookeeper().getSessionTimeout(),
                                    properties.getZookeeper().getConnectionTimeout(),
                                    ZKStringSerializer$.MODULE$);

            ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(properties.getZookeeper().getHost()), false);

            if (AdminUtils.topicExists(zkUtils, tenant)) {
                AdminUtils.deleteTopic(zkUtils, tenant);
            }
            log.info("Kafka topic deleted for tenantKey: {}, time = {} ms", tenant, stopWatch.getTime());

        } finally {
            if (zkClient != null) {
                zkClient.close();
            }
        }
    }

    /**
     * Send 'tenant management' command to system topic.
     *
     * @param tenant  the tenant to manage
     * @param command the command (e.g. CREATE, DELETE, ...)
     */
    public void sendCommand(String tenant, String command) {
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
        event.setMessageSource(appName);
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
