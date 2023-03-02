package com.icthh.xm.ms.timeline.web.rest;

import com.icthh.xm.ms.timeline.AbstractSpringBootTest;
import com.icthh.xm.ms.timeline.service.TimelineService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.Order.desc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the XmTimelineResource controller advice.
 *
 * @see XmTimelineResource
 */
@WithMockUser(authorities = {"SUPER-ADMIN"})
public class SortIntTest extends AbstractSpringBootTest {

    @Autowired
    private XmTimelineResource controller;

    @MockBean
    private TimelineService timelineService;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new SortHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    public void getTimelines_shouldUseDefaultSort_whenSortParamIsNotPresent() throws Exception {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("limit", "0");

        mockMvc.perform(get("/api/timelines").params(requestParams).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        Sort expectedSort = Sort.by(DESC, "startDate");

        verify(timelineService).getTimelines(any(), any(), any(), any(), any(), any(), any(), any(), eq(0), eq(expectedSort));
    }

    @Test
    public void getTimelines_shouldUseCustomSort_whenSortPresent() throws Exception {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("limit", "0");
        requestParams.add("sort", "id,desc");
        requestParams.add("sort", "startDate,desc");

        mockMvc.perform(get("/api/timelines").params(requestParams).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        Sort expectedSort = Sort.by(desc("id"), desc("startDate"));

        verify(timelineService).getTimelines(any(), any(), any(), any(), any(), any(), any(), any(), eq(0), eq(expectedSort));
    }

}
