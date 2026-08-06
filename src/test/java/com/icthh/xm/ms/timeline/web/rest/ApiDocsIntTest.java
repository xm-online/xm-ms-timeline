package com.icthh.xm.ms.timeline.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.icthh.xm.ms.timeline.AbstractSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Guards the OpenAPI document springdoc serves at /v3/api-docs - the endpoint the gateway and
 * Swagger UI read. Nothing else in the suite touches it, so a broken springdoc setup used to go
 * unnoticed until runtime.
 */
public class ApiDocsIntTest extends AbstractSpringBootTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void apiDocsIsServedAsOpenApi3() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.openapi").value(org.hamcrest.Matchers.startsWith("3.")))
            .andExpect(jsonPath("$.paths").isNotEmpty());
    }

    @Test
    public void apiDocsCarriesTimelineOperationMetadata() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            // these come from @Tag/@Operation/@Parameter/@ApiResponse on the timeline resources;
            // the springfox-era io.swagger.annotations they replaced were silently ignored here
            .andExpect(jsonPath("$.paths['/api/timelines/v2'].get.summary")
                           .value("Get list of timelines (version 2)"))
            .andExpect(jsonPath("$.paths['/api/timelines/v2'].get.tags[0]").value("timelines"))
            .andExpect(jsonPath("$.paths['/api/timelines/v2'].get.responses.200.description")
                           .value("Successful retrieval of timelines"))
            .andExpect(jsonPath(
                "$.paths['/api/timelines/v2'].get.parameters[?(@.name == 'msName')].description")
                           .value(org.hamcrest.Matchers.hasItem("Microservices name for timeline filter")));
    }
}
