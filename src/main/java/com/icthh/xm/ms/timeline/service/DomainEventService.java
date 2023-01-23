package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomainEventService {

    private final TimelineService timelineService;

    public void processEvent(DomainEvent domainEvent) {
        log.debug("processEvent {}", domainEvent);
        timelineService.insertTimelines(domainEvent);
    }
}
