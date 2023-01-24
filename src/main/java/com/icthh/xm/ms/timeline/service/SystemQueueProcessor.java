package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.commons.messaging.event.system.SystemEvent;

public interface SystemQueueProcessor {

    void processSystemEvent(SystemEvent systemEvent);

}
