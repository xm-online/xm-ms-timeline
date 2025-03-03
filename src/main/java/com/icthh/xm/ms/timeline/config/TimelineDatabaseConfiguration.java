package com.icthh.xm.ms.timeline.config;

import static com.icthh.xm.ms.timeline.config.Constants.RDBMS_IMPL;
import static com.icthh.xm.ms.timeline.config.TimelineDatabaseConfiguration.DBDEV;
import static com.icthh.xm.ms.timeline.config.TimelineDatabaseConfiguration.DBPROD;

import com.icthh.xm.commons.migration.db.config.DatabaseConfiguration;
import com.icthh.xm.commons.migration.db.tenant.SchemaResolver;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import tech.jhipster.config.JHipsterConstants;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = RDBMS_IMPL)
@Configuration
@EnableJpaRepositories("com.icthh.xm.ms.timeline.repository.jpa")
@EntityScan("com.icthh.xm.ms.timeline.domain.*")
@EnableTransactionManagement
@ComponentScan(basePackages = "com.icthh.xm.commons.migration")
@Import({HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class})
@Profile({DBDEV, DBPROD})
public class TimelineDatabaseConfiguration extends DatabaseConfiguration {

    private static final String JPA_PACKAGES = "com.icthh.xm.ms.timeline.domain";
    static final String DBDEV = "dbdev";
    static final String DBPROD = "dbprod";

    public TimelineDatabaseConfiguration(Environment env, JpaProperties jpaProperties, SchemaResolver schemaResolver) {
        super(env, jpaProperties, schemaResolver);
    }

    /**
     * Open the TCP port for the H2 database, so it is available remotely.
     *
     * @return the H2 database TCP server
     * @throws SQLException if the server failed to start
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
    public Server h2TCPServer() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers");
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Override
    public String getJpaPackages() {
        return JPA_PACKAGES;
    }
}
