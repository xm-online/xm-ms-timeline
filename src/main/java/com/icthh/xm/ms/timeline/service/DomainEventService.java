package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import com.icthh.xm.commons.lep.LogicExtensionPoint;
import com.icthh.xm.commons.lep.spring.LepService;
import com.icthh.xm.ms.timeline.lep.keyresolver.DomainEventAggregateTypeResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@LepService(group = "topic")
public class DomainEventService {

    private final TimelineService timelineService;

    @LogicExtensionPoint(value = "ProcessEvent", resolver = DomainEventAggregateTypeResolver.class)
    public void processEvent(DomainEvent domainEvent) {
        log.debug("processEvent {}", domainEvent);
        timelineService.insertTimelines(domainEvent);
    }
}
