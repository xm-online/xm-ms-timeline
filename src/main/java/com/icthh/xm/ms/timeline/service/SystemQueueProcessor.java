package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.commons.lep.LogicExtensionPoint;
import com.icthh.xm.commons.lep.spring.LepService;
import com.icthh.xm.commons.logging.LoggingAspectConfig;
import com.icthh.xm.commons.messaging.event.system.SystemEvent;

@LepService(group = "system.queue")
public interface SystemQueueProcessor {

    @LoggingAspectConfig(resultDetails = false)
    @LogicExtensionPoint(value = "ProcessQueueEvent")
    void processQueueEvent(SystemEvent systemEvent);
}
