package com.icthh.xm.ms.timeline.service.dto;

import com.icthh.xm.commons.tenant.JsonMapperUtils;
import com.icthh.xm.ms.timeline.AbstractUnitTest;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.module.SimpleModule;

import static org.assertj.core.api.Assertions.assertThat;

class HeaderValueDeserializerUnitTest extends AbstractUnitTest {

    private final ObjectMapper mapper = JsonMapperUtils.getDefaultJsonMapper()
        .rebuild()
        .addModule(new SimpleModule().addDeserializer(String.class, new HeaderValueDeserializer()))
        .build();

    @Test
    void collapsesMultiValueHeaderArrayIntoJoinedString() throws JacksonException {
        String json = """
            {"requestHeaders":{"accept-encoding":["gzip","deflate"],"x-tenant":"TENANT"}}
        """;

        TimelineEvent event = mapper.readValue(json, TimelineEvent.class);

        assertThat(event.requestHeaders())
            .containsEntry("accept-encoding", "gzip, deflate")
            .containsEntry("x-tenant", "TENANT");
    }

    @Test
    void collapsesSingleElementArrayWithoutSeparator() throws JacksonException {
        String json = """
            {"requestHeaders":{"accept-encoding":["gzip"]}}
        """;

        TimelineEvent event = mapper.readValue(json, TimelineEvent.class);

        assertThat(event.requestHeaders()).containsEntry("accept-encoding", "gzip");
    }

    @Test
    void collapsesEmptyArrayIntoEmptyString() throws JacksonException {
        String json = """
            {"requestHeaders":{"x-scheme":[]}}
        """;

        TimelineEvent event = mapper.readValue(json, TimelineEvent.class);

        assertThat(event.requestHeaders()).containsEntry("x-scheme", "");
    }

    @Test
    void handlesArrayValuesInBothRequestAndResponseHeaders() throws JacksonException {
        String json = """
            {"requestHeaders":{"accept-encoding":["gzip","deflate"],"x-scheme":["https","http"]},
             "responseHeaders":{"vary":["Origin","Accept-Encoding"],"content-type":"application/json"}}
        """;

        TimelineEvent event = mapper.readValue(json, TimelineEvent.class);

        assertThat(event.requestHeaders())
            .containsEntry("accept-encoding", "gzip, deflate")
            .containsEntry("x-scheme", "https, http");
        assertThat(event.responseHeaders())
            .containsEntry("vary", "Origin, Accept-Encoding")
            .containsEntry("content-type", "application/json");
    }

    @Test
    void deserializesPayloadWithMixedFieldTypes() throws JacksonException {
        String json = """
            {"tenant":"TEST","msName":"uaa","login":null,"httpStatusCode":401,"execTime":17,
             "requestHeaders":{"host":"localhost","accept-encoding":["gzip","deflate"]},
             "responseHeaders":{"content-type":"application/json","vary":["Origin","Accept-Encoding"]}}
        """;

        TimelineEvent event = mapper.readValue(json, TimelineEvent.class);

        assertThat(event.tenant()).isEqualTo("TEST");
        assertThat(event.login()).isNull();
        assertThat(event.httpStatusCode()).isEqualTo(401);
        assertThat(event.execTime()).isEqualTo(17L);
        assertThat(event.requestHeaders())
            .containsEntry("host", "localhost")
            .containsEntry("accept-encoding", "gzip, deflate");
        assertThat(event.responseHeaders())
            .containsEntry("content-type", "application/json")
            .containsEntry("vary", "Origin, Accept-Encoding");
    }
}
