package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import com.icthh.xm.commons.lep.XmLepScriptConfigServerResourceLoader;
import com.icthh.xm.commons.lep.api.LepManagementService;
import com.icthh.xm.commons.migration.db.tenant.DropSchemaResolver;
import com.icthh.xm.commons.security.XmAuthenticationContextHolder;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TimelineApp.class, DropSchemaResolver.class})
@TestPropertySource(properties = {
    "application.timeline-service-impl = logger"
})
@ActiveProfiles(JHipsterConstants.SPRING_PROFILE_TEST)
public class DomainEventServiceIntTest {

    public static final String DEFAULT_TENANT = "RESINTTEST";

    @Autowired
    private XmLepScriptConfigServerResourceLoader leps;

    @Autowired
    private XmAuthenticationContextHolder authContextHolder;

    @Autowired
    private LepManagementService lepManagementService;

    @Autowired
    private TenantContextHolder tenantContextHolder;

    @Autowired
    private DomainEventService domainEventService;

    @MockitoBean
    private TimelineService timelineService;

    private final List<String> lepsForCleanUp = new ArrayList<>();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        TenantContextUtils.setTenant(tenantContextHolder, DEFAULT_TENANT);

        lepManagementService.beginThreadContext();
    }

    @AfterEach
    public void afterTest() {
        lepsForCleanUp.forEach(it -> leps.onRefresh(it, null));

        tenantContextHolder.getPrivilegedContext().destroyCurrentContext();
        lepManagementService.endThreadContext();
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
