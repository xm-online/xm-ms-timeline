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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

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
            "/config/tenants/{tenantName}/timeline/default-topics-spec.yml");

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
        configurations.add(Configuration.of().path("/config/tenants/{tenantName}/timeline/default-topics-spec.yml")
            .content(getTopicContent()).build());

        verify(tenantConfigRepository).createConfigsFullPath(eq("newtenant"), eq(configurations));

    }

    private String getTopicContent() {
        return "---\n" +
            "topics:\n" +
            "- key: \"db\"\n" +
            "  typeKey: \"event.db\"\n" +
            "  topicName: \"event.newtenant.db\"\n" +
            "  retriesCount: 4\n" +
            "  backOffPeriod: 1\n" +
            "  deadLetterQueue: null\n" +
            "  groupId: \"timeline\"\n" +
            "  logBody: true\n" +
            "  maxPollInterval: null\n" +
            "  isolationLevel: null\n" +
            "- key: \"web\"\n" +
            "  typeKey: \"event.web\"\n" +
            "  topicName: \"event.newtenant.web\"\n" +
            "  retriesCount: 4\n" +
            "  backOffPeriod: 1\n" +
            "  deadLetterQueue: null\n" +
            "  groupId: \"timeline\"\n" +
            "  logBody: true\n" +
            "  maxPollInterval: null\n" +
            "  isolationLevel: null\n" +
            "- key: \"lep\"\n" +
            "  typeKey: \"event.lep\"\n" +
            "  topicName: \"event.newtenant.lep\"\n" +
            "  retriesCount: 4\n" +
            "  backOffPeriod: 1\n" +
            "  deadLetterQueue: null\n" +
            "  groupId: \"timeline\"\n" +
            "  logBody: true\n" +
            "  maxPollInterval: null\n" +
            "  isolationLevel: null\n";
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
