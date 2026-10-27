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
@ConfigurationProperties(prefix = "application")
@Getter
@Setter
public class ApplicationProperties {
    private final Retry retry = new Retry();
    private List<String> tenantIgnoredPathList = new ArrayList<>();
    private Zookeeper zookeeper = new Zookeeper();
    private final Lep lep = new Lep();
    private String kafkaSystemTopic;
    private String kafkaSystemQueue;
    private Integer kafkaMetadataMaxAge;
    private String tenantPropertiesPathPattern;
    private String tenantPropertiesName;
    private String timelineServiceImpl;
    private String dbSchemaSuffix;
    private String domainEventTopicsPathPattern;
    private String domainEventTopicsName;
    private String specificationTemplatesPathPattern;
    private int maxPageSize = 1000;
    private String configPrivateKey;
    private final ApiDocs apiDocs = new ApiDocs();
    private final Prometheus prometheus = new Prometheus();
    private final Management management = new Management();
    private final TenantCache tenantCache = new TenantCache();
    private final TenantMemoryCache tenantMemoryCache = new TenantMemoryCache();
    private final XmAutoConfigurationFilter xmAutoConfigurationFilter = new XmAutoConfigurationFilter();
    private final Deduplication deduplication = new Deduplication();

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

    @Data
    public static class ApiDocs {
        private boolean privilegeCheck;
    }

    public static class Prometheus {
        private boolean publicAccess;

        public boolean isPublic() {
            return publicAccess;
        }

        public void setPublic(boolean publicAccess) {
            this.publicAccess = publicAccess;
        }
    }

    @Data
    public static class TenantCache {
        private String enabled;
    }

    @Data
    public static class TenantMemoryCache {
        private String enabled;
    }

    @Data
    public static class XmAutoConfigurationFilter {
        private boolean disabled;
    }

    @Data
    public static class Deduplication {
        private String strategy;
    }

    @Data
    public static class Management {
        private final Access access = new Access();
        private final Info info = new Info();
        private final Logs logs = new Logs();

        @Data
        public static class Access {
            private String role;
        }

        public static class Info {
            private boolean publicAccess;

            public boolean isPublic() {
                return publicAccess;
            }

            public void setPublic(boolean publicAccess) {
                this.publicAccess = publicAccess;
            }
        }

        @Data
        public static class Logs {
            private boolean systemAdminOnly;
        }
    }
}
