package com.icthh.xm.ms.timeline.service;

import com.datastax.driver.core.Cluster;
import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import com.icthh.xm.commons.lep.XmLepScriptConfigServerResourceLoader;
import com.icthh.xm.commons.migration.db.tenant.DropSchemaResolver;
import com.icthh.xm.commons.security.XmAuthenticationContextHolder;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.lep.api.LepManager;
import com.icthh.xm.ms.timeline.TimelineApp;
import com.icthh.xm.ms.timeline.config.SecurityBeanOverrideConfiguration;
import io.github.jhipster.config.JHipsterConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static com.icthh.xm.commons.lep.XmLepConstants.THREAD_CONTEXT_KEY_AUTH_CONTEXT;
import static com.icthh.xm.commons.lep.XmLepConstants.THREAD_CONTEXT_KEY_TENANT_CONTEXT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TimelineApp.class, SecurityBeanOverrideConfiguration.class, DropSchemaResolver.class})
@TestPropertySource(properties = {
    "application.timeline-service-impl = logger",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration"
})
@ActiveProfiles(JHipsterConstants.SPRING_PROFILE_TEST)
public class DomainEventServiceIntTest {

    public static final String DEFAULT_TENANT = "RESINTTEST";

    @Autowired
    private XmLepScriptConfigServerResourceLoader leps;

    @Autowired
    private XmAuthenticationContextHolder authContextHolder;

    @Autowired
    private LepManager lepManager;

    @Autowired
    private TenantContextHolder tenantContextHolder;

    @Autowired
    private DomainEventService domainEventService;

    @MockBean
    private TimelineService timelineService;

    @MockBean
    private Cluster cluster;

    @MockBean
    private CassandraProperties cassandraProperties;

    private final List<String> lepsForCleanUp = new ArrayList<>();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        TenantContextUtils.setTenant(tenantContextHolder, DEFAULT_TENANT);

        lepManager.beginThreadContext(ctx -> {
            ctx.setValue(THREAD_CONTEXT_KEY_TENANT_CONTEXT, tenantContextHolder.getContext());
            ctx.setValue(THREAD_CONTEXT_KEY_AUTH_CONTEXT, authContextHolder.getContext());
        });
    }

    @After
    public void afterTest() {
        lepsForCleanUp.forEach(it -> leps.onRefresh(it, null));

        tenantContextHolder.getPrivilegedContext().destroyCurrentContext();
        lepManager.endThreadContext();
    }

    @Test
    public void test_lep() {
        String prefix = "/config/tenants/" + DEFAULT_TENANT + "/timeline/lep/topic/";
        String key = prefix + "ProcessEvent$$partyIndividual$$around.groovy";
        String body = "def domainEvent = lepContext.inArgs?.domainEvent\n" +
            "domainEvent.msName = \"new-ms-name\"\n" +
            "return lepContext.lep.proceed(domainEvent)";

        leps.onRefresh(key, body);
        lepsForCleanUp.add(key);

        DomainEvent domainEvent = new DomainEvent();
        domainEvent.setAggregateType("partyIndividual");
        domainEvent.setMsName("old-ms-name");

        domainEventService.processEvent(domainEvent);

        domainEvent.setMsName("new-ms-name");
        verify(timelineService, times(1)).insertTimelines(eq(domainEvent));
    }
}
