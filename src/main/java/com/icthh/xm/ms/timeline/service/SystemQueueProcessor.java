package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.commons.lep.spring.LepService;
import com.icthh.xm.commons.messaging.event.system.SystemEvent;

@LepService(group = "systemEvent")
public interface SystemQueueProcessor {

    void processSystemEvent(SystemEvent systemEvent);
}
