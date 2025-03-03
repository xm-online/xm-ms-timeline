package com.icthh.xm.ms.timeline.config;

import static com.icthh.xm.ms.timeline.config.Constants.CHANGE_LOG_PATH;
import static com.icthh.xm.ms.timeline.config.Constants.RDBMS_IMPL;
import static com.icthh.xm.ms.timeline.config.DatabaseConfiguration.DBDEV;
import static com.icthh.xm.ms.timeline.config.DatabaseConfiguration.DBPROD;
import static org.hibernate.cfg.AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER;
import static org.hibernate.cfg.AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.icthh.xm.commons.migration.db.XmMultiTenantSpringLiquibase;
import com.icthh.xm.commons.migration.db.XmSpringLiquibase;
import com.icthh.xm.commons.migration.db.tenant.SchemaResolver;
import jakarta.persistence.EntityManagerFactory;
import tech.jhipster.config.JHipsterConstants;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import liquibase.integration.spring.MultiTenantSpringLiquibase;
import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@ConditionalOnProperty(name = "application.timeline-service-impl", havingValue = RDBMS_IMPL)
@Configuration
@EnableJpaRepositories("com.icthh.xm.ms.timeline.repository.jpa")
@EnableTransactionManagement
@RequiredArgsConstructor
@ComponentScan(basePackages = "com.icthh.xm.commons.migration")
@Import({HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class})
@Profile({DBDEV, DBPROD})
public class DatabaseConfiguration {

    private static final String JPA_PACKAGES = "com.icthh.xm.ms.timeline.domain";
    static final String DBDEV = "dbdev";
    static final String DBPROD = "dbprod";

    private final Environment env;
    private final JpaProperties jpaProperties;
    private final SchemaResolver schemaResolver;

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

    /**
     * Liquibase configuration
     *
     * @param dataSource          db data source
     * @param liquibaseProperties configuration properties
     * @return instance of the {@link SpringLiquibase} bin
     */
    @Bean
    public SpringLiquibase liquibase(DataSource dataSource, LiquibaseProperties liquibaseProperties) {
        schemaResolver.createSchemas(dataSource);
        SpringLiquibase liquibase = new XmSpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(CHANGE_LOG_PATH);
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_NO_LIQUIBASE))) {
            liquibase.setShouldRun(false);
        } else {
            liquibase.setShouldRun(liquibaseProperties.isEnabled());
            log.debug("Configuring Liquibase");
        }
        return liquibase;
    }

    @Bean
    @DependsOn("liquibase")
    public MultiTenantSpringLiquibase multiTenantLiquibase(
        DataSource dataSource,
        LiquibaseProperties liquibaseProperties) {
        MultiTenantSpringLiquibase liquibase = new XmMultiTenantSpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(CHANGE_LOG_PATH);
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        liquibase.setSchemas(schemaResolver.getSchemas());
        if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_NO_LIQUIBASE))) {
            liquibase.setShouldRun(false);
        } else {
            liquibase.setShouldRun(liquibaseProperties.isEnabled());
            log.debug("Configuring Liquibase");
        }
        return liquibase;
    }

    @Bean
    public Hibernate5Module hibernate5Module() {
        return new Hibernate5Module();
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
        DataSource dataSource,
        MultiTenantConnectionProvider multiTenantConnectionProviderImpl,
        CurrentTenantIdentifierResolver currentTenantIdentifierResolverImpl) {

        Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());
        properties.put(MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProviderImpl);
        properties.put(MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolverImpl);

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(JPA_PACKAGES);
        em.setJpaVendorAdapter(jpaVendorAdapter());
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
