package com.icthh.xm.ms.timeline.service.mapper;

import com.icthh.xm.ms.timeline.domain.XmTimeline;
import com.icthh.xm.ms.timeline.service.dto.TimelineDto;
import com.icthh.xm.ms.timeline.service.dto.TimelineEvent;
import org.h2.util.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
        @Mapping(target = "entityId",  source = "aggregateId", qualifiedByName = "aggregateIdToEntityId"),
        @Mapping(target = "entityTypeKey", source = "aggregateType"),
        @Mapping(target = "channelType", source = "clientId")
    })
    TimelineEvent xmTimelineToTimelineEvent(XmTimeline xmTimeline);

    TimelineDto xmTimelineToTimeline(XmTimeline xmTimeline);

    List<TimelineEvent> xmTimelineToTimelineEvent(List<XmTimeline> xmTimelines);

    List<TimelineDto> xmTimelineToTimeline(List<XmTimeline> xmTimelines);

    @Named("aggregateIdToEntityId")
    default Long aggregateIdToEntityId(String aggregateId) {
        if (aggregateId == null) {
            return null;
        }

        if (StringUtils.isNumber(aggregateId)) {
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

}
