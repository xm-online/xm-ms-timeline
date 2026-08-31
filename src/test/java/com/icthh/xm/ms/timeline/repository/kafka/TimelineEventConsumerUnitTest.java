package com.icthh.xm.ms.timeline.repository.kafka;

import com.icthh.xm.commons.tenant.PrivilegedTenantContext;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.ms.timeline.AbstractUnitTest;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.service.dto.TimelineEvent;
import com.icthh.xm.ms.timeline.service.mapper.XmTimelineMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimelineEventConsumerUnitTest extends AbstractUnitTest {

    @Mock
    private XmTimelineMapper xmTimelineMapper;

    @Mock
    private TenantContextHolder tenantContextHolder;

    @Mock
    private PrivilegedTenantContext privilegedTenantContext;

    @InjectMocks
    private TimelineEventConsumer consumer;

    @Test
    void consumesMessageWithMultiValueHeaderArraysAndJoinsThem() {
        XmTimeline mapped = new XmTimeline();
        mapped.setTenant("TENANT");
        when(xmTimelineMapper.timelineEventToXmTimeline(any())).thenReturn(mapped);
        when(tenantContextHolder.getPrivilegedContext()).thenReturn(privilegedTenantContext);

        String json = """
            {"tenant":"TEST","msName":"uaa","login":null,"httpMethod":"POST","httpStatusCode":401,"execTime":17,
             "requestHeaders":{"host":"localhost","accept-encoding":["gzip","deflate"],"x-scheme":["https","http"]},
             "responseHeaders":{"content-type":"application/json"}}
        """;
        ConsumerRecord<String, String> record = new ConsumerRecord<>("TENANT", 0, 0L, "key", json);
        consumer.consumeEvent(record);

        ArgumentCaptor<TimelineEvent> captor = ArgumentCaptor.forClass(TimelineEvent.class);
        verify(xmTimelineMapper).timelineEventToXmTimeline(captor.capture());

        TimelineEvent event = captor.getValue();
        assertThat(event.requestHeaders())
            .containsEntry("host", "localhost")
            .containsEntry("accept-encoding", "gzip, deflate")
            .containsEntry("x-scheme", "https, http");
        assertThat(event.responseHeaders()).containsEntry("content-type", "application/json");

        verify(privilegedTenantContext).execute(any(), any(), any());
    }
}
