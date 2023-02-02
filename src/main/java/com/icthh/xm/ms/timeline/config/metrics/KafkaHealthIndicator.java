package com.icthh.xm.ms.timeline.config.metrics;

import com.icthh.xm.ms.timeline.config.ApplicationProperties;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.ConfigResource.Type;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Slf4j
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(name = "application.kafkaHealthCheck.enabled", havingValue = "true")
public class KafkaHealthIndicator extends AbstractHealthIndicator {

    private static final String REPLICATION_PROPERTY = "transaction.state.log.replication.factor";

    private final KafkaAdmin admin;
    private final ApplicationProperties applicationProperties;

    @Override
    protected void doHealthCheck(Builder builder) {
        DescribeClusterOptions describeClusterOptions = new DescribeClusterOptions()
            .timeoutMs(applicationProperties.getKafkaHealthCheck().getConnectionTimeout());

        try(AdminClient adminClient = AdminClient.create(admin.getConfigurationProperties())) {
            DescribeClusterResult describeCluster = adminClient.describeCluster(describeClusterOptions);
            try {

                String brokerId = describeCluster.controller().get().idString();
                int replicationFactor = getReplicationFactor(brokerId, adminClient);
                int nodes = describeCluster.nodes().get().size();
                Status status = nodes >= replicationFactor ? Status.UP : Status.DOWN;
                builder.status(status)
                       .withDetail("clusterId", describeCluster.clusterId().get())
                       .withDetail("nodeCount", nodes)
                       .build();
                log.debug("Run kafka health check. Result: OK");
            } catch (InterruptedException | ExecutionException e) {
                builder.down().withException(e).build();
            }
        }
    }

    private int getReplicationFactor(String brokerId, AdminClient adminClient)
        throws ExecutionException, InterruptedException {
        ConfigResource configResource = new ConfigResource(Type.BROKER, brokerId);
        Map<ConfigResource, Config> kafkaConfig = adminClient
            .describeConfigs(Collections.singletonList(configResource)).all().get();
        Config brokerConfig = kafkaConfig.get(configResource);
        return Integer.parseInt(brokerConfig.get(REPLICATION_PROPERTY).value());
    }
}
