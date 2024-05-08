package com.icthh.xm.ms.timeline.service.mapper;

import com.icthh.xm.commons.domainevent.domain.DbDomainEventPayload;
import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import com.icthh.xm.commons.domainevent.domain.DomainEventPayload;
import com.icthh.xm.commons.domainevent.domain.HttpDomainEventPayload;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.service.dto.TimelineDto;
import com.icthh.xm.ms.timeline.service.dto.TimelineEvent;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Mapper(config = BaseMapper.class)
public interface XmTimelineMapper {

    Logger LOGGER = LoggerFactory.getLogger(XmTimelineMapper.class);

    @Mappings({
        @Mapping(target = "aggregateId", expression = "java(timelineEvent.entityId() == null ? null : String.valueOf(timelineEvent.entityId()))"),
        @Mapping(target = "aggregateType", source = "entityTypeKey"),
        @Mapping(target = "clientId", source = "channelType"),
        @Mapping(target = "source", ignore = true),
        @Mapping(target = "data", ignore = true),
        @Mapping(target = "entityBefore", ignore = true),
        @Mapping(target = "entityAfter", ignore = true)
    })
    XmTimeline timelineEventToXmTimeline(TimelineEvent timelineEvent);

    @Mappings({
        @Mapping(target = "entityId", source = "aggregateId", qualifiedByName = "aggregateIdToEntityId"),
        @Mapping(target = "entityTypeKey", source = "aggregateType"),
        @Mapping(target = "channelType", source = "clientId")
    })
    TimelineEvent xmTimelineToTimelineEvent(XmTimeline xmTimeline);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "rid", ignore = true),
        @Mapping(target = "login", ignore = true),
        @Mapping(target = "entityKey", ignore = true),
        @Mapping(target = "browser", ignore = true),
        @Mapping(target = "opSystem", ignore = true),
        @Mapping(target = "startDate", source = "eventDate"),
        @Mapping(target = "operationName", source = "operation"),
        @Mapping(target = "data", source = "payload.data"),
        @Mapping(target = "entityBefore", ignore = true),
        @Mapping(target = "entityAfter", ignore = true),
        @Mapping(target = "httpMethod", ignore = true),
        @Mapping(target = "operationUrl", ignore = true),
        @Mapping(target = "operationQueryString", ignore = true),
        @Mapping(target = "requestLength", ignore = true),
        @Mapping(target = "requestBody", ignore = true),
        @Mapping(target = "responseLength", ignore = true),
        @Mapping(target = "responseBody", ignore = true),
        @Mapping(target = "requestHeaders", ignore = true),
        @Mapping(target = "responseHeaders", ignore = true),
        @Mapping(target = "httpStatusCode", ignore = true),
        @Mapping(target = "execTime", ignore = true)
    })
    XmTimeline domainEventToXmTimeline(DomainEvent domainEvent);

    @AfterMapping
    default void enrichXmTimeline(DomainEvent domainEvent, @MappingTarget XmTimeline xmTimeline) {
        DomainEventPayload domainEventPayload = domainEvent.getPayload();
        if (domainEventPayload == null) {
            return;
        }

        if (domainEventPayload instanceof DbDomainEventPayload dbDomainEventPayload) {
            xmTimeline.setEntityBefore(dbDomainEventPayload.getBefore());
            xmTimeline.setEntityAfter(dbDomainEventPayload.getAfter());
        }

        if (domainEventPayload instanceof HttpDomainEventPayload httpDomainEventPayload) {
            xmTimeline.setHttpMethod(httpDomainEventPayload.getMethod());
            xmTimeline.setRequestLength(httpDomainEventPayload.getRequestLength());
            xmTimeline.setRequestBody(httpDomainEventPayload.getRequestBody());
            xmTimeline.setResponseLength(httpDomainEventPayload.getResponseLength());
            xmTimeline.setResponseBody(httpDomainEventPayload.getResponseBody());
            xmTimeline.setRequestHeaders(convertHeaders(httpDomainEventPayload.getRequestHeaders()));
            xmTimeline.setResponseHeaders(convertHeaders(httpDomainEventPayload.getResponseHeaders()));
            xmTimeline.setExecTime(httpDomainEventPayload.getExecTime());
            xmTimeline.setOperationUrl(httpDomainEventPayload.getUrl());
            xmTimeline.setOperationQueryString(httpDomainEventPayload.getQueryString());
            xmTimeline.setHttpStatusCode(httpDomainEventPayload.getResponseCode());
        }
    }

    @Mappings({
        @Mapping(target = "txId", ignore = true),
        @Mapping(target = "aggregateName", ignore = true),
        @Mapping(target = "validFrom", ignore = true),
        @Mapping(target = "validTo", ignore = true),
        @Mapping(target = "meta", ignore = true)
    })
    TimelineDto xmTimelineToTimelineDto(XmTimeline xmTimeline);

    List<TimelineEvent> xmTimelineToTimelineEvent(List<XmTimeline> xmTimelines);

    List<TimelineDto> xmTimelineToTimelineDto(List<XmTimeline> xmTimelines);

    @Named("aggregateIdToEntityId")
    default Long aggregateIdToEntityId(String aggregateId) {
        if (aggregateId == null) {
            return null;
        }

        if (StringUtils.isNumeric(aggregateId)) {
            try {
                return Long.parseLong(aggregateId);
            } catch (NumberFormatException e) {
                LOGGER.error("Error parse aggregateId", e);
                return null;
            }
        } else {
            LOGGER.debug("The field aggregateId is not a number");
            return null;
        }
    }

    default Map<String, String> convertHeaders(Map<String, List<String>> headers) {
        if (isEmpty(headers)) {
            return null;
        }

        return headers.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> String.join(", ", e.getValue())));
    }

}
