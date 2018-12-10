package com.icthh.xm.ms.timeline.config.cassandra;

import static com.icthh.xm.ms.timeline.config.Constants.CASSANDRA_IMPL;

import com.codahale.metrics.MetricRegistry;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.core.TupleType;
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.ReconnectionPolicy;
import com.datastax.driver.core.policies.RetryPolicy;
import com.datastax.driver.extras.codecs.jdk8.InstantCodec;
import com.datastax.driver.extras.codecs.jdk8.LocalDateCodec;
import com.datastax.driver.extras.codecs.jdk8.ZonedDateTimeCodec;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.ms.timeline.repository.cassandra.EntityMappingRepository;
import com.icthh.xm.ms.timeline.repository.cassandra.TimelineCassandraRepository;
import com.icthh.xm.ms.timeline.service.TenantPropertiesService;
import io.github.jhipster.config.JHipsterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = CASSANDRA_IMPL)
@EnableConfigurationProperties(CassandraProperties.class)
@Profile({JHipsterConstants.SPRING_PROFILE_DEVELOPMENT, JHipsterConstants.SPRING_PROFILE_PRODUCTION})
public class CassandraConfiguration {

    @Value("${spring.data.cassandra.protocolVersion:V4}")
    private ProtocolVersion protocolVersion;

    @Autowired(required = false)
    MetricRegistry metricRegistry;

    private final Logger log = LoggerFactory.getLogger(CassandraConfiguration.class);

    @Bean
    public Cluster cluster(CassandraProperties properties) {
        Cluster.Builder builder = Cluster.builder()
            .withClusterName(properties.getClusterName())
            .withProtocolVersion(protocolVersion)
            .withPort(getPort(properties));

        if (properties.getUsername() != null) {
            builder.withCredentials(properties.getUsername(), properties.getPassword());
        }
        if (properties.getCompression() != null) {
            builder.withCompression(properties.getCompression());
        }
        if (properties.getLoadBalancingPolicy() != null) {
            LoadBalancingPolicy policy = instantiate(properties.getLoadBalancingPolicy());
            builder.withLoadBalancingPolicy(policy);
        }
        builder.withQueryOptions(getQueryOptions(properties));
        if (properties.getReconnectionPolicy() != null) {
            ReconnectionPolicy policy = instantiate(properties.getReconnectionPolicy());
            builder.withReconnectionPolicy(policy);
        }
        if (properties.getRetryPolicy() != null) {
            RetryPolicy policy = instantiate(properties.getRetryPolicy());
            builder.withRetryPolicy(policy);
        }
        builder.withSocketOptions(getSocketOptions(properties));
        if (properties.isSsl()) {
            builder.withSSL();
        }
        String points = properties.getContactPoints();
        builder.addContactPoints(StringUtils.commaDelimitedListToStringArray(points));

        Cluster cluster = builder.build();

        TupleType tupleType = cluster.getMetadata()
            .newTupleType(DataType.timestamp(), DataType.varchar());

        cluster.getConfiguration().getCodecRegistry()
            .register(LocalDateCodec.instance)
            .register(InstantCodec.instance)
            .register(new ZonedDateTimeCodec(tupleType));

        if (metricRegistry != null) {
            cluster.init();
            metricRegistry.registerAll(cluster.getMetrics().getRegistry());
        }

        return cluster;
    }

    protected int getPort(CassandraProperties properties) {
        return properties.getPort();
    }

    public static <T> T instantiate(Class<T> type) {
        return BeanUtils.instantiate(type);
    }

    private QueryOptions getQueryOptions(CassandraProperties properties) {
        QueryOptions options = new QueryOptions();
        if (properties.getConsistencyLevel() != null) {
            options.setConsistencyLevel(properties.getConsistencyLevel());
        }
        if (properties.getSerialConsistencyLevel() != null) {
            options.setSerialConsistencyLevel(properties.getSerialConsistencyLevel());
        }
        options.setFetchSize(properties.getFetchSize());
        return options;
    }

    private SocketOptions getSocketOptions(CassandraProperties properties) {
        SocketOptions options = new SocketOptions();
        options.setConnectTimeoutMillis(properties.getConnectTimeoutMillis());
        options.setReadTimeoutMillis(properties.getReadTimeoutMillis());
        return options;
    }

    @Bean(destroyMethod = "close")
    public Session session(Cluster cluster) {
        log.debug("Configuring Cassandra session");
        return cluster.connect();
    }

    @Bean
    public EntityMappingRepository entityMappingRepository(Session session) {
        return new EntityMappingRepository(session);
    }

    @Bean
    public TimelineCassandraRepository timelineRepository(TenantPropertiesService tenantPropertiesService, TenantContextHolder tenantContextHolder, Session session) {
        return new TimelineCassandraRepository(tenantPropertiesService, tenantContextHolder, session);
    }
}
