package com.icthh.xm.ms.timeline.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icthh.xm.commons.domainevent.domain.DbDomainEventPayload;
import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import com.icthh.xm.commons.domainevent.domain.DomainEventPayload;
import com.icthh.xm.commons.domainevent.domain.HttpDomainEventPayload;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class XmTimelineMapperUnitTest {

    private static final String JSON = """
        {"typekey":"SOME_TYPE","data":{"name":"Test","height":12}}
        """;
    private static final String HEADER_X_TOTAL_COUNT = "X-Total-Count";
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";

    private XmTimelineMapper xmTimelineMapper = Mappers.getMapper(XmTimelineMapper.class);

    @Test
    public void testDomainEventToXmTimelineWithDbDomainEventPayload() throws JsonProcessingException {
        Map<String, Object> data = convertJsonToMap(JSON);

        DbDomainEventPayload dbDomainEventPayload = new DbDomainEventPayload();
        dbDomainEventPayload.setBefore(null);
        dbDomainEventPayload.setAfter(data);

        DomainEvent domainEvent = createTestDomainEvent(dbDomainEventPayload);

        XmTimeline xmTimeline = xmTimelineMapper.domainEventToXmTimeline(domainEvent);

        assertThat(xmTimeline.getId()).isNull();
        assertThat(xmTimeline.getEntityBefore()).isNull();
        assertThat(xmTimeline.getEntityAfter()).isEqualTo(data);
    }

    @Test
    public void testDomainEventToXmTimelineWithHttpDomainEventPayload() throws JsonProcessingException {
        Map<String, Object> data = convertJsonToMap(JSON);
        Map<String, List<String>> headerMap = Map.of(HEADER_X_TOTAL_COUNT, List.of("34"), HEADER_ACCEPT_ENCODING, List.of("gzip", "deflate", "br"));
        HttpHeaders headers = HttpHeaders.of(headerMap, (s1, s2) -> true);

        HttpDomainEventPayload httpDomainEventPayload = new HttpDomainEventPayload();
        httpDomainEventPayload.setData(data);
        httpDomainEventPayload.setResponseHeaders(null);
        httpDomainEventPayload.setRequestHeaders(headers);

        DomainEvent domainEvent = createTestDomainEvent(httpDomainEventPayload);

        XmTimeline xmTimeline = xmTimelineMapper.domainEventToXmTimeline(domainEvent);

        assertThat(xmTimeline.getId()).isNull();
        assertThat(xmTimeline.getData()).isEqualTo(data);
        assertThat(xmTimeline.getResponseHeaders()).isNull();
        assertThat(xmTimeline.getRequestHeaders()).hasSize(2);
        assertThat(xmTimeline.getRequestHeaders()).containsEntry(HEADER_X_TOTAL_COUNT, "34");
        assertThat(xmTimeline.getRequestHeaders()).containsEntry(HEADER_ACCEPT_ENCODING, "gzip, deflate, br");
    }

    private DomainEvent createTestDomainEvent(DomainEventPayload payload) {
        return DomainEvent.builder()
            .payload(payload)
            .build();
    }

    public static Map<String, Object> convertJsonToMap(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, Map.class);
    }

}
