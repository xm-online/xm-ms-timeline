package com.icthh.xm.ms.timeline.config.tenant;

import com.icthh.xm.commons.config.client.repository.TenantConfigRepository;
import com.icthh.xm.commons.config.domain.Configuration;
import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.commons.migration.db.tenant.provisioner.TenantDatabaseProvisioner;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenantendpoint.TenantManager;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantAbilityCheckerProvisioner;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantConfigProvisioner;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantListProvisioner;
import com.icthh.xm.ms.timeline.config.ApplicationProperties;
import com.icthh.xm.ms.timeline.service.tenant.provisioner.TenantKafkaProvisioner;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TenantManagerConfigurationUnitTest {

    private TenantManager tenantManager;

    private TenantConfigProvisioner configProvisioner;

    @Mock
    private TenantContextHolder tenantContextHolder;

    @Mock
    private TenantAbilityCheckerProvisioner abilityCheckerProvisioner;
    @Mock
    private TenantDatabaseProvisioner databaseProvisioner;
    @Mock
    private TenantListProvisioner tenantListProvisioner;
    @Mock
    private TenantConfigRepository tenantConfigRepository;
    @Mock
    private TenantKafkaProvisioner kafkaProvisioner;
    @Mock
    private ApplicationProperties applicationProperties;

    @Before
    public void setup() {

        when(tenantContextHolder.getTenantKey()).thenReturn("newtenant");

        TenantManagerConfiguration configuration = new TenantManagerConfiguration(tenantContextHolder);

        MockitoAnnotations.initMocks(this);

        when(applicationProperties.getTenantPropertiesPathPattern()).thenReturn(
            "/config/tenants/{tenantName}/timeline/timeline.yml");
        when(applicationProperties.getDomainEventTopicsPathPattern()).thenReturn(
            "/config/tenants/{tenantName}/timeline/default-topic-consumers.yml");

        configProvisioner = spy(configuration.tenantConfigProvisioner(tenantConfigRepository, applicationProperties));

        tenantManager = configuration.tenantManager(abilityCheckerProvisioner,
                                                    databaseProvisioner,
                                                    configProvisioner,
                                                    tenantListProvisioner,
                                                    kafkaProvisioner);
    }

    @Test
    public void testCreateTenantConfigProvisioning() {

        tenantManager.createTenant(new Tenant().tenantKey("newtenant"));

        List<Configuration> configurations = new ArrayList<>();
        configurations.add(Configuration.of().path("/config/tenants/{tenantName}/timeline/timeline.yml").build());
        configurations.add(Configuration.of().path("/config/tenants/{tenantName}/timeline/default-topic-consumers.yml")
            .content(getSpecificationConfig("config/specs/topic-consumers.yml")).build());

        verify(tenantConfigRepository).createConfigsFullPath(eq("newtenant"), eq(configurations));

    }

    @SneakyThrows
    private String getSpecificationConfig(String configPath) {
        InputStream cfgInputStream = new ClassPathResource(configPath).getInputStream();
        return IOUtils.toString(cfgInputStream, UTF_8);
    }

    @Test
    public void testCreateTenantProvisioningOrder() {

        tenantManager.createTenant(new Tenant().tenantKey("newtenant"));

        InOrder inOrder = Mockito.inOrder(abilityCheckerProvisioner,
                                          tenantListProvisioner,
                                          databaseProvisioner,
                                          configProvisioner,
                                          kafkaProvisioner);

        inOrder.verify(abilityCheckerProvisioner).createTenant(any(Tenant.class));
        inOrder.verify(tenantListProvisioner).createTenant(any(Tenant.class));
        inOrder.verify(databaseProvisioner).createTenant(any(Tenant.class));
        inOrder.verify(kafkaProvisioner).createTenant(any(Tenant.class));
        inOrder.verify(configProvisioner).createTenant(any(Tenant.class));

        verifyNoMoreInteractions(abilityCheckerProvisioner,
                                 tenantListProvisioner,
                                 databaseProvisioner,
                                 configProvisioner,
                                 kafkaProvisioner);
    }

}
