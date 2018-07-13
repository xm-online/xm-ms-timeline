package com.icthh.xm.ms.timeline.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
public class JpaRepositoriesConfig {

//    @Configuration
//    @ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = ServiceConfiguration.POSTGRES_IMPL)
//    @EnableJpaRepositories
//    @EnableTransactionManagement
//    public static class PostgreSqlConfiguration {
//
//    }

    @Configuration
    @ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = ServiceConfiguration.H2DB_IMPL)
    @EnableJpaRepositories("com.icthh.xm.ms.timeline.repository.jpa")
    @EnableTransactionManagement
    public static class H2dbConfiguration {

        @Bean
        public DataSource dataSource() {
            EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
            return builder.setType(EmbeddedDatabaseType.H2).build();
        }
    }

}
