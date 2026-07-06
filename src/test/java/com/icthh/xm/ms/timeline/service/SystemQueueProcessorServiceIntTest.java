package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.commons.lep.XmLepScriptConfigServerResourceLoader;
import com.icthh.xm.commons.messaging.event.system.SystemEvent;
import com.icthh.xm.commons.security.XmAuthenticationContextHolder;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.lep.api.LepManager;
import com.icthh.xm.ms.timeline.TimelineApp;
import tech.jhipster.config.JHipsterConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.icthh.xm.commons.lep.XmLepConstants.THREAD_CONTEXT_KEY_AUTH_CONTEXT;
import static com.icthh.xm.commons.lep.XmLepConstants.THREAD_CONTEXT_KEY_TENANT_CONTEXT;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TimelineApp.class})
@TestPropertySource(properties = {
        "application.timeline-service-impl = logger",
})
@ActiveProfiles(JHipsterConstants.SPRING_PROFILE_TEST)
public class SystemQueueProcessorServiceIntTest {

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

    @MockitoBean
    private TimelineService timelineService;


    private final List<String> lepsForCleanUp = new ArrayList<>();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        TenantContextUtils.setTenant(tenantContextHolder, DEFAULT_TENANT);

        lepManager.beginThreadContext(ctx -> {
            ctx.setValue(THREAD_CONTEXT_KEY_TENANT_CONTEXT, tenantContextHolder.getContext());
            ctx.setValue(THREAD_CONTEXT_KEY_AUTH_CONTEXT, authContextHolder.getContext());
        });
    }

    @AfterEach
    public void afterTest() {
        lepsForCleanUp.forEach(it -> leps.onRefresh(it, null));

        tenantContextHolder.getPrivilegedContext().destroyCurrentContext();
        lepManager.endThreadContext();
    }

    @Test
    public void test_lep() {
        String prefix = "/config/tenants/" + DEFAULT_TENANT + "/timeline/lep/system/queue/";
        String key = prefix + "ProcessQueueEvent$$around.groovy";
        String body = "throw new RuntimeException(\"Hello\")";

        leps.onRefresh(key, body);
        lepsForCleanUp.add(key);

        SystemEvent systemEvent = new SystemEvent();
        systemEvent.setEventId(UUID.randomUUID().toString());
        systemEvent.setEventType("eventType");

        assertThrows(RuntimeException.class, () -> {
            systemQueueProcessorService.handleSystemEvent(systemEvent);
        });
    }
}
