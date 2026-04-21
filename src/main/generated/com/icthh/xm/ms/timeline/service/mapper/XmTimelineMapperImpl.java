package com.icthh.xm.ms.timeline.service.mapper;

import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import com.icthh.xm.commons.domainevent.domain.DomainEventPayload;
import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.service.dto.TimelineDto;
import com.icthh.xm.ms.timeline.service.dto.TimelineEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-21T19:48:07+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Azul Systems, Inc.)"
)
@Component
public class XmTimelineMapperImpl implements XmTimelineMapper {

    @Override
    public XmTimeline timelineEventToXmTimeline(TimelineEvent timelineEvent) {
        if ( timelineEvent == null ) {
            return null;
        }

        XmTimeline xmTimeline = new XmTimeline();

        xmTimeline.setAggregateType( timelineEvent.entityTypeKey() );
        xmTimeline.setClientId( timelineEvent.channelType() );
        xmTimeline.setId( timelineEvent.id() );
        xmTimeline.setRid( timelineEvent.rid() );
        xmTimeline.setLogin( timelineEvent.login() );
        xmTimeline.setUserKey( timelineEvent.userKey() );
        xmTimeline.setTenant( timelineEvent.tenant() );
        xmTimeline.setMsName( timelineEvent.msName() );
        xmTimeline.setOperationName( timelineEvent.operationName() );
        xmTimeline.setEntityKey( timelineEvent.entityKey() );
        xmTimeline.setOperationUrl( timelineEvent.operationUrl() );
        xmTimeline.setOperationQueryString( timelineEvent.operationQueryString() );
        xmTimeline.setHttpMethod( timelineEvent.httpMethod() );
        xmTimeline.setHttpStatusCode( timelineEvent.httpStatusCode() );
        xmTimeline.setStartDate( timelineEvent.startDate() );
        xmTimeline.setRequestBody( timelineEvent.requestBody() );
        xmTimeline.setRequestLength( timelineEvent.requestLength() );
        xmTimeline.setResponseBody( timelineEvent.responseBody() );
        xmTimeline.setResponseLength( timelineEvent.responseLength() );
        Map<String, String> map = timelineEvent.requestHeaders();
        if ( map != null ) {
            xmTimeline.setRequestHeaders( new LinkedHashMap<String, String>( map ) );
        }
        Map<String, String> map1 = timelineEvent.responseHeaders();
        if ( map1 != null ) {
            xmTimeline.setResponseHeaders( new LinkedHashMap<String, String>( map1 ) );
        }
        xmTimeline.setExecTime( timelineEvent.execTime() );
        xmTimeline.setBrowser( timelineEvent.browser() );
        xmTimeline.setOpSystem( timelineEvent.opSystem() );

        xmTimeline.setAggregateId( timelineEvent.entityId() == null ? null : String.valueOf(timelineEvent.entityId()) );

        return xmTimeline;
    }

    @Override
    public TimelineEvent xmTimelineToTimelineEvent(XmTimeline xmTimeline) {
        if ( xmTimeline == null ) {
            return null;
        }

        TimelineEvent.TimelineEventBuilder timelineEvent = TimelineEvent.builder();

        timelineEvent.entityId( aggregateIdToEntityId( xmTimeline.getAggregateId() ) );
        timelineEvent.entityTypeKey( xmTimeline.getAggregateType() );
        timelineEvent.channelType( xmTimeline.getClientId() );
        timelineEvent.id( xmTimeline.getId() );
        timelineEvent.rid( xmTimeline.getRid() );
        timelineEvent.login( xmTimeline.getLogin() );
        timelineEvent.userKey( xmTimeline.getUserKey() );
        timelineEvent.tenant( xmTimeline.getTenant() );
        timelineEvent.msName( xmTimeline.getMsName() );
        timelineEvent.operationName( xmTimeline.getOperationName() );
        timelineEvent.entityKey( xmTimeline.getEntityKey() );
        timelineEvent.operationUrl( xmTimeline.getOperationUrl() );
        timelineEvent.operationQueryString( xmTimeline.getOperationQueryString() );
        timelineEvent.httpMethod( xmTimeline.getHttpMethod() );
        timelineEvent.httpStatusCode( xmTimeline.getHttpStatusCode() );
        timelineEvent.startDate( xmTimeline.getStartDate() );
        timelineEvent.requestBody( xmTimeline.getRequestBody() );
        timelineEvent.requestLength( xmTimeline.getRequestLength() );
        timelineEvent.responseBody( xmTimeline.getResponseBody() );
        timelineEvent.responseLength( xmTimeline.getResponseLength() );
        Map<String, String> map = xmTimeline.getRequestHeaders();
        if ( map != null ) {
            timelineEvent.requestHeaders( new LinkedHashMap<String, String>( map ) );
        }
        Map<String, String> map1 = xmTimeline.getResponseHeaders();
        if ( map1 != null ) {
            timelineEvent.responseHeaders( new LinkedHashMap<String, String>( map1 ) );
        }
        timelineEvent.execTime( xmTimeline.getExecTime() );
        timelineEvent.browser( xmTimeline.getBrowser() );
        timelineEvent.opSystem( xmTimeline.getOpSystem() );

        return timelineEvent.build();
    }

    @Override
    public XmTimeline domainEventToXmTimeline(DomainEvent domainEvent) {
        if ( domainEvent == null ) {
            return null;
        }

        XmTimeline xmTimeline = new XmTimeline();

        xmTimeline.setStartDate( domainEvent.getEventDate() );
        xmTimeline.setOperationName( domainEvent.getOperation() );
        Map<String, Object> data = domainEventPayloadData( domainEvent );
        Map<String, Object> map = data;
        if ( map != null ) {
            xmTimeline.setData( new LinkedHashMap<String, Object>( map ) );
        }
        xmTimeline.setUserKey( domainEvent.getUserKey() );
        xmTimeline.setTenant( domainEvent.getTenant() );
        xmTimeline.setMsName( domainEvent.getMsName() );
        xmTimeline.setAggregateId( domainEvent.getAggregateId() );
        xmTimeline.setAggregateType( domainEvent.getAggregateType() );
        xmTimeline.setClientId( domainEvent.getClientId() );
        xmTimeline.setSource( domainEvent.getSource() );

        enrichXmTimeline( domainEvent, xmTimeline );

        return xmTimeline;
    }

    @Override
    public TimelineDto xmTimelineToTimelineDto(XmTimeline xmTimeline) {
        if ( xmTimeline == null ) {
            return null;
        }

        TimelineDto.TimelineDtoBuilder timelineDto = TimelineDto.builder();

        timelineDto.id( xmTimeline.getId() );
        timelineDto.rid( xmTimeline.getRid() );
        timelineDto.login( xmTimeline.getLogin() );
        timelineDto.userKey( xmTimeline.getUserKey() );
        timelineDto.tenant( xmTimeline.getTenant() );
        timelineDto.msName( xmTimeline.getMsName() );
        timelineDto.operationName( xmTimeline.getOperationName() );
        timelineDto.aggregateId( xmTimeline.getAggregateId() );
        timelineDto.entityKey( xmTimeline.getEntityKey() );
        timelineDto.aggregateType( xmTimeline.getAggregateType() );
        timelineDto.operationUrl( xmTimeline.getOperationUrl() );
        timelineDto.operationQueryString( xmTimeline.getOperationQueryString() );
        timelineDto.httpMethod( xmTimeline.getHttpMethod() );
        timelineDto.httpStatusCode( xmTimeline.getHttpStatusCode() );
        timelineDto.startDate( xmTimeline.getStartDate() );
        timelineDto.requestBody( xmTimeline.getRequestBody() );
        timelineDto.requestLength( xmTimeline.getRequestLength() );
        timelineDto.responseBody( xmTimeline.getResponseBody() );
        timelineDto.responseLength( xmTimeline.getResponseLength() );
        timelineDto.clientId( xmTimeline.getClientId() );
        Map<String, String> map = xmTimeline.getRequestHeaders();
        if ( map != null ) {
            timelineDto.requestHeaders( new LinkedHashMap<String, String>( map ) );
        }
        Map<String, String> map1 = xmTimeline.getResponseHeaders();
        if ( map1 != null ) {
            timelineDto.responseHeaders( new LinkedHashMap<String, String>( map1 ) );
        }
        timelineDto.execTime( xmTimeline.getExecTime() );
        timelineDto.browser( xmTimeline.getBrowser() );
        timelineDto.opSystem( xmTimeline.getOpSystem() );
        timelineDto.source( xmTimeline.getSource() );
        Map<String, Object> map2 = xmTimeline.getData();
        if ( map2 != null ) {
            timelineDto.data( new LinkedHashMap<String, Object>( map2 ) );
        }
        Map<String, Object> map3 = xmTimeline.getEntityBefore();
        if ( map3 != null ) {
            timelineDto.entityBefore( new LinkedHashMap<String, Object>( map3 ) );
        }
        Map<String, Object> map4 = xmTimeline.getEntityAfter();
        if ( map4 != null ) {
            timelineDto.entityAfter( new LinkedHashMap<String, Object>( map4 ) );
        }

        return timelineDto.build();
    }

    @Override
    public List<TimelineEvent> xmTimelineToTimelineEvent(List<XmTimeline> xmTimelines) {
        if ( xmTimelines == null ) {
            return null;
        }

        List<TimelineEvent> list = new ArrayList<TimelineEvent>( xmTimelines.size() );
        for ( XmTimeline xmTimeline : xmTimelines ) {
            list.add( xmTimelineToTimelineEvent( xmTimeline ) );
        }

        return list;
    }

    @Override
    public List<TimelineDto> xmTimelineToTimelineDto(List<XmTimeline> xmTimelines) {
        if ( xmTimelines == null ) {
            return null;
        }

        List<TimelineDto> list = new ArrayList<TimelineDto>( xmTimelines.size() );
        for ( XmTimeline xmTimeline : xmTimelines ) {
            list.add( xmTimelineToTimelineDto( xmTimeline ) );
        }

        return list;
    }

    private Map<String, Object> domainEventPayloadData(DomainEvent domainEvent) {
        DomainEventPayload payload = domainEvent.getPayload();
        if ( payload == null ) {
            return null;
        }
        return payload.getData();
    }
}
