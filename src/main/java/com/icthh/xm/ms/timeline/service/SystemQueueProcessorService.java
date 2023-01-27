package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.commons.lep.LogicExtensionPoint;
import com.icthh.xm.commons.lep.spring.LepService;
import com.icthh.xm.commons.messaging.event.system.SystemEvent;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@LepService(group = "system.queue")
public class SystemQueueProcessorService {

    private final List<SystemQueueProcessor> systemQueueProcessors;

    @LogicExtensionPoint(value = "ProcessQueueEvent")
    public void handleSystemEvent(SystemEvent systemEvent) {
        if (systemQueueProcessors != null) {
            systemQueueProcessors.forEach(processor -> processor.processQueueEvent(systemEvent));
        }
    }
}
