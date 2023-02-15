package com.icthh.xm.ms.timeline.service.mapper;

import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.service.dto.Timeline;
import com.icthh.xm.ms.timeline.service.dto.TimelineEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class XmTimelineMapper {

    public XmTimeline timelineEventToXmTimeline(TimelineEvent timelineEvent) {
        if (timelineEvent == null) {
            return null;
        } else {
            return XmTimeline.builder()
                .id(timelineEvent.id())
                .rid(timelineEvent.rid())
                .login(timelineEvent.login())
                .userKey(timelineEvent.userKey())
                .tenant(timelineEvent.tenant())
                .msName(timelineEvent.msName())
                .operationName(timelineEvent.operationName())
                .aggregateId(timelineEvent.entityId() == null ? null : String.valueOf(timelineEvent.entityId()))
                .entityKey(timelineEvent.entityKey())
                .aggregateType(timelineEvent.entityTypeKey())
                .operationUrl(timelineEvent.operationUrl())
                .operationQueryString(timelineEvent.operationQueryString())
                .httpMethod(timelineEvent.httpMethod())
                .httpStatusCode(timelineEvent.httpStatusCode())
                .startDate(timelineEvent.startDate())
                .requestBody(timelineEvent.requestBody())
                .requestLength(timelineEvent.requestLength())
                .responseBody(timelineEvent.responseBody())
                .responseLength(timelineEvent.responseLength())
                .clientId(timelineEvent.channelType())
                .requestHeaders(timelineEvent.requestHeaders())
                .responseHeaders(timelineEvent.responseHeaders())
                .execTime(timelineEvent.execTime())
                .browser(timelineEvent.browser())
                .opSystem(timelineEvent.opSystem())
                .build();
        }
    }

    public TimelineEvent xmTimelineToTimelineEvent(XmTimeline xmTimeline) {
        if (xmTimeline == null) {
            return null;
        } else {
            return TimelineEvent.builder()
                .id(xmTimeline.getId())
                .rid(xmTimeline.getRid())
                .login(xmTimeline.getLogin())
                .userKey(xmTimeline.getUserKey())
                .tenant(xmTimeline.getTenant())
                .msName(xmTimeline.getMsName())
                .operationName(xmTimeline.getOperationName())
                .entityId(aggregateIdToEntityId(xmTimeline.getAggregateId()))
                .entityKey(xmTimeline.getEntityKey())
                .entityTypeKey(xmTimeline.getAggregateType())
                .operationUrl(xmTimeline.getOperationUrl())
                .operationQueryString(xmTimeline.getOperationQueryString())
                .httpMethod(xmTimeline.getHttpMethod())
                .httpStatusCode(xmTimeline.getHttpStatusCode())
                .startDate(xmTimeline.getStartDate())
                .requestBody(xmTimeline.getRequestBody())
                .requestLength(xmTimeline.getRequestLength())
                .responseBody(xmTimeline.getResponseBody())
                .responseLength(xmTimeline.getResponseLength())
                .channelType(xmTimeline.getClientId())
                .requestHeaders(xmTimeline.getRequestHeaders())
                .responseHeaders(xmTimeline.getResponseHeaders())
                .execTime(xmTimeline.getExecTime())
                .browser(xmTimeline.getBrowser())
                .opSystem(xmTimeline.getOpSystem())
                .build();
        }
    }

    public List<TimelineEvent> xmTimelineToTimelineEvent(List<XmTimeline> xmTimelines) {
        if (xmTimelines == null) {
            return null;
        }

        List<TimelineEvent> list = new ArrayList<>(xmTimelines.size());
        for (XmTimeline xmTimeline : xmTimelines) {
            list.add(xmTimelineToTimelineEvent(xmTimeline));
        }

        return list;
    }

    public Timeline xmTimelineToTimeline(XmTimeline xmTimeline) {
        if (xmTimeline == null) {
            return null;
        } else {
            return Timeline.builder()
                .id(xmTimeline.getId())
                .rid(xmTimeline.getRid())
                .login(xmTimeline.getLogin())
                .userKey(xmTimeline.getUserKey())
                .tenant(xmTimeline.getTenant())
                .msName(xmTimeline.getMsName())
                .operationName(xmTimeline.getOperationName())
                .aggregateId(xmTimeline.getAggregateId())
                .entityKey(xmTimeline.getEntityKey())
                .aggregateType(xmTimeline.getAggregateType())
                .operationUrl(xmTimeline.getOperationUrl())
                .operationQueryString(xmTimeline.getOperationQueryString())
                .httpMethod(xmTimeline.getHttpMethod())
                .httpStatusCode(xmTimeline.getHttpStatusCode())
                .startDate(xmTimeline.getStartDate())
                .requestBody(xmTimeline.getRequestBody())
                .requestLength(xmTimeline.getRequestLength())
                .responseBody(xmTimeline.getResponseBody())
                .responseLength(xmTimeline.getResponseLength())
                .clientId(xmTimeline.getClientId())
                .requestHeaders(xmTimeline.getRequestHeaders())
                .responseHeaders(xmTimeline.getResponseHeaders())
                .execTime(xmTimeline.getExecTime())
                .browser(xmTimeline.getBrowser())
                .opSystem(xmTimeline.getOpSystem())
                .source(xmTimeline.getSource())
                .data(xmTimeline.getData())
                .entityBefore(xmTimeline.getEntityBefore())
                .entityAfter(xmTimeline.getEntityAfter())
                .build();
        }
    }

    public List<Timeline> xmTimelineToTimeline(List<XmTimeline> xmTimelines) {
        if (xmTimelines == null) {
            return null;
        }

        List<Timeline> list = new ArrayList<>(xmTimelines.size());
        for (XmTimeline xmTimeline : xmTimelines) {
            list.add(xmTimelineToTimeline(xmTimeline));
        }

        return list;
    }

    private Long aggregateIdToEntityId(String aggregateId) {
        if (aggregateId != null) {
            try {
                return Long.parseLong(aggregateId);
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }
    }

}
