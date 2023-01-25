package com.icthh.xm.ms.timeline.service.kafka;

import com.datastax.driver.core.Cluster;
import com.icthh.xm.commons.lep.XmLepScriptConfigServerResourceLoader;
import com.icthh.xm.commons.messaging.event.system.SystemEvent;
import com.icthh.xm.commons.security.XmAuthenticationContextHolder;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.lep.api.LepManager;
import com.icthh.xm.ms.timeline.TimelineApp;
import com.icthh.xm.ms.timeline.config.SecurityBeanOverrideConfiguration;
import com.icthh.xm.ms.timeline.service.SystemQueueProcessorService;
import com.icthh.xm.ms.timeline.service.TimelineService;
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
import java.util.UUID;

import static com.icthh.xm.commons.lep.XmLepConstants.THREAD_CONTEXT_KEY_AUTH_CONTEXT;
import static com.icthh.xm.commons.lep.XmLepConstants.THREAD_CONTEXT_KEY_TENANT_CONTEXT;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TimelineApp.class, SecurityBeanOverrideConfiguration.class})
@TestPropertySource(properties = {
        "application.timeline-service-impl = logger",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration"
})
@ActiveProfiles(JHipsterConstants.SPRING_PROFILE_TEST)
public class SystemQueueConsumerIntTest {

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
    private SystemQueueProcessorService systemQueueProcessorService;

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

    @Test(expected = RuntimeException.class)
    public void test_lep() {
        String prefix = "/config/tenants/" + DEFAULT_TENANT + "/timeline/lep/system/queue/";
        String key = prefix + "ProcessQueueEvent$$around.groovy";
        String body = "throw new RuntimeException(\"Hello\")";

        leps.onRefresh(key, body);
        lepsForCleanUp.add(key);

        SystemEvent systemEvent = new SystemEvent();
        systemEvent.setEventId(UUID.randomUUID().toString());
        systemEvent.setEventType("eventType");

        systemQueueProcessorService.handleSystemEvent(systemEvent);
    }
}
