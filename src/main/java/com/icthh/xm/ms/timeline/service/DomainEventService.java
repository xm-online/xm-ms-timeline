package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DomainEventService {

    public void processEvent(DomainEvent domainEvent) {
        log.info("Event {}", domainEvent);
    }
}
