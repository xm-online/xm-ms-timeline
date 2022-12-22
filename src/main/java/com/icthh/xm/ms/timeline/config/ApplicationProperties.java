package com.icthh.xm.ms.timeline.config;

import java.util.ArrayList;
import java.util.List;

import com.icthh.xm.commons.lep.TenantScriptStorage;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to JHipster.
 * Properties are configured in the application.yml file.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Getter
@Setter
public class ApplicationProperties {
    private final Retry retry = new Retry();
    private Cassandra cassandra = new Cassandra();
    private List<String> tenantIgnoredPathList = new ArrayList<>();
    private Zookeeper zookeeper = new Zookeeper();
    private final Lep lep = new Lep();
    private String kafkaSystemTopic;
    private String kafkaSystemQueue;
    private String tenantPropertiesPathPattern;
    private String tenantPropertiesName;
    private String timelineServiceImpl;
    private String dbSchemaSuffix;
    private String domainEventPathPattern;
    private String domainEventName;

    private final KafkaHealthCheck kafkaHealthCheck = new KafkaHealthCheck();

    @Data
    public static class KafkaHealthCheck {
        private boolean enabled;
        private int connectionTimeout;
    }

    @Getter
    @Setter
    private static class Retry {
        private int maxAttempts;
        private long delay;
        private int multiplier;
    }

    @Getter
    @Setter
    public static class Cassandra {
        private String migrationFolder;
        private String keyspaceCreateCql;
        private String keyspaceSwitchCql;
    }

    @Getter
    @Setter
    public static class Zookeeper {
        private String host;
        private int partitions;
        private short replication;
        private int sessionTimeout;
        private int connectionTimeout;
    }

    @Getter
    @Setter
    public static class Lep {
        private TenantScriptStorage tenantScriptStorage;
    }
}
