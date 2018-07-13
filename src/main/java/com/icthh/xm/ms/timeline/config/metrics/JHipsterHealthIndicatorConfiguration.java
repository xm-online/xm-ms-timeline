package com.icthh.xm.ms.timeline.config.metrics;
import com.datastax.driver.core.Session;
import com.icthh.xm.ms.timeline.config.ServiceConfiguration;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = ServiceConfiguration.CASSANDRA_IMPL)
public class JHipsterHealthIndicatorConfiguration {

    private final Session session;

    public JHipsterHealthIndicatorConfiguration(Session session) {
        this.session = session;
    }

    @Bean
    public HealthIndicator cassandraHealthIndicator() {
        return new CassandraHealthIndicator(session);
    }
}
