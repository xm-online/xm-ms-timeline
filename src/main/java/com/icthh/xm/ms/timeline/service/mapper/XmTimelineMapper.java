package com.icthh.xm.ms.timeline.service.mapper;

import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.service.dto.Timeline;
import com.icthh.xm.ms.timeline.service.dto.TimelineEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(config = BaseMapper.class)
public interface XmTimelineMapper {

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
        @Mapping(target = "entityId",  source = "aggregateId", qualifiedByName = "aggregateIdToEntityId"),
        @Mapping(target = "entityTypeKey", source = "aggregateType"),
        @Mapping(target = "channelType", source = "clientId")
    })
    TimelineEvent xmTimelineToTimelineEvent(XmTimeline xmTimeline);

    Timeline xmTimelineToTimeline(XmTimeline xmTimeline);

    List<TimelineEvent> xmTimelineToTimelineEvent(List<XmTimeline> xmTimelines);

    List<Timeline> xmTimelineToTimeline(List<XmTimeline> xmTimelines);

    @Named("aggregateIdToEntityId")
    default Long aggregateIdToEntityId(String aggregateId) {
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
