package com.icthh.xm.ms.timeline.config;

import com.icthh.xm.commons.migration.db.liquibase.LiquibaseRunner;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;

@Configuration
public class LiquibaseRunnerConfiguration {

    @Bean
    public LiquibaseRunner liquibaseRunner(ResourceLoader resourceLoader, DataSource dataSource,
                                           LiquibaseProperties liquibaseProperties) {
        return new LiquibaseRunner(resourceLoader, dataSource, liquibaseProperties);
    }
}
