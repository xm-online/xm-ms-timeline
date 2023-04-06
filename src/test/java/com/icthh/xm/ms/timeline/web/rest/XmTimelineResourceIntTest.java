package com.icthh.xm.ms.timeline.web.rest;

import com.icthh.xm.ms.timeline.AbstractSpringBootTest;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.repository.jpa.TimelineJpaRepository;
import com.icthh.xm.ms.timeline.service.TenantPropertiesService;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(authorities = {"SUPER-ADMIN"})
@TestPropertySource(properties = {"application.timeline-service-impl = rdbms"})
public class XmTimelineResourceIntTest extends AbstractSpringBootTest {

    private static final String API_URL = "/api/timelines";
    private static final String API_URL_V2 = "/api/timelines/v2";

    private static final String HEADER_X_TOTAL_COUNT = "X-Total-Count";

    private static final String PARAM_LIMIT = "limit";
    private static final String PARAM_AGGREGATE_ID = "aggregateId";
    private static final String PARAM_SOURCE = "source";
    private static final int VALUE_LIMIT = 10;
    private static final String VALUE_AGGREGATE_ID = "test_aggregate_id";
    private static final String VALUE_SOURCE = "test_source";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    private MockMvc mockMvc;

    @Autowired
    private XmTimelineResource xmTimelineResource;

    @Autowired
    private TimelineJpaRepository timelineJpaRepository;

    @MockBean
    private TenantPropertiesService tenantPropertiesService;

    public static XmTimeline createEntity(String aggregateId, String source) {
        XmTimeline entity = new XmTimeline();
        entity.setStartDate(Instant.now());
        entity.setAggregateId(aggregateId);
        entity.setSource(source);

        return entity;
    }

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(xmTimelineResource)
            .setCustomArgumentResolvers(new SortHandlerMethodArgumentResolver())
            .build();
    }

    @BeforeEach
    public void init() {
        Mockito
            .when(tenantPropertiesService.getTenantProps())
            .thenReturn(null);
    }

    @Test
    @Transactional
    public void getTimelinesWithoutFiltering() throws Exception {
        List<XmTimeline> entities = Arrays.asList(
            createEntity(String.valueOf(count.incrementAndGet()), VALUE_SOURCE),
            createEntity(String.valueOf(count.incrementAndGet()), "web")
        );
        timelineJpaRepository.saveAllAndFlush(entities);

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(PARAM_LIMIT, String.valueOf(VALUE_LIMIT));

        mockMvc
            .perform(get(API_URL).params(requestParams))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.timelines.*", Matchers.hasSize(2)))
            .andExpect(jsonPath("$.next").value(IsNull.nullValue()));
    }

    @Test
    @Transactional
    public void getTimelinesWithFilterSource() throws Exception {
        List<XmTimeline> entities = Arrays.asList(
            createEntity(UUID.randomUUID().toString(), VALUE_SOURCE),
            createEntity(String.valueOf(count.incrementAndGet()), "db")
        );
        timelineJpaRepository.saveAllAndFlush(entities);

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(PARAM_LIMIT, String.valueOf(VALUE_LIMIT));
        requestParams.add(PARAM_SOURCE, VALUE_SOURCE);

        mockMvc
            .perform(get(API_URL).params(requestParams))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.timelines.*", Matchers.hasSize(1)))
            .andExpect(jsonPath("$.next").value(IsNull.nullValue()));
    }

    @Test
    @Transactional
    public void getTimelinesV2WithoutFiltering() throws Exception {
        List<XmTimeline> entities = Arrays.asList(
            createEntity(UUID.randomUUID().toString(), VALUE_SOURCE),
            createEntity(UUID.randomUUID().toString(), "web")
        );
        timelineJpaRepository.saveAllAndFlush(entities);

        mockMvc
            .perform(get(API_URL_V2))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.*", Matchers.hasSize(2)))
            .andExpect(header().stringValues(HEADER_X_TOTAL_COUNT, String.valueOf(entities.size())));
    }

    @Test
    @Transactional
    public void getTimelinesV2WithFilterAggregateId() throws Exception {
        List<XmTimeline> entities = Arrays.asList(
            createEntity(VALUE_AGGREGATE_ID, VALUE_SOURCE),
            createEntity(String.valueOf(count.incrementAndGet()), VALUE_SOURCE)
        );
        timelineJpaRepository.saveAllAndFlush(entities);

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(PARAM_AGGREGATE_ID, VALUE_AGGREGATE_ID);

        mockMvc
            .perform(get(API_URL_V2).params(requestParams))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.*", Matchers.hasSize(1)))
            .andExpect(jsonPath("$.[0].aggregateId").value(VALUE_AGGREGATE_ID))
            .andExpect(header().stringValues(HEADER_X_TOTAL_COUNT, "1"));
    }

}
