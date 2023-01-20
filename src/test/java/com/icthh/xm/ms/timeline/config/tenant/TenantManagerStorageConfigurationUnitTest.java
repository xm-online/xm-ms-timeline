package com.icthh.xm.ms.timeline.config.tenant;

import com.datastax.driver.core.Cluster;
import com.icthh.xm.commons.migration.db.tenant.DropSchemaResolver;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantAbilityCheckerProvisioner;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantConfigProvisioner;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantListProvisioner;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantProvisioner;
import com.icthh.xm.ms.timeline.config.ApplicationProperties;
import com.icthh.xm.ms.timeline.service.tenant.provisioner.TenantCassandraStorageProvisioner;
import com.icthh.xm.ms.timeline.service.tenant.provisioner.TenantKafkaProvisioner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
    TenantManagerConfiguration.class
})
public class TenantManagerStorageConfigurationUnitTest {

    @Qualifier(value = "storageTenantProvisioner")
    @Autowired
    TenantProvisioner storageTenantProvisioner;

    @MockBean
    TenantAbilityCheckerProvisioner abilityCheckerProvisioner;

    @MockBean
    TenantConfigProvisioner configProvisioner;

    @MockBean
    TenantListProvisioner listProvisioner;

    @MockBean
    TenantKafkaProvisioner kafkaProvisioner;

    @MockBean
    DataSource dataSource;

    @MockBean
    LiquibaseProperties liquibaseProperties;

    @MockBean
    DropSchemaResolver dropSchemaResolver;

    @MockBean
    CassandraProperties cassandraProperties;

    @MockBean
    private ApplicationProperties applicationProperties;

    @MockBean
    private TenantContextHolder tenantContextHolder;

    @MockBean
    private Cluster cluster;

    @Test
    public void testCreateDefaultTenantLoggerStorageProvisioner() {

        Assert.assertEquals(TenantCassandraStorageProvisioner.class, storageTenantProvisioner.getClass());

    }

}
