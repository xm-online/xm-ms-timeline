package com.icthh.xm.ms.timeline.service.cache;

import com.icthh.xm.commons.domainevent.domain.DomainEvent;

public interface EventDeduplicationStrategy {

    public String getHash(DomainEvent event);

    public boolean cachedExists(DomainEvent event);
}
