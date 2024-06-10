package com.icthh.xm.ms.timeline.service.cache;

import com.icthh.xm.commons.cache.TenantCacheManager;
import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;

@Slf4j
public class MemoryLepDeduplicationStrategy implements EventDeduplicationStrategy {

    private final TenantCacheManager lepCacheManager;

    public MemoryLepDeduplicationStrategy(TenantCacheManager lepCacheManager) {
        this.lepCacheManager = lepCacheManager;
    }

    @Override
    public String getHash(DomainEvent event) {
        return String.valueOf(DomainEventHashCodeWrapper.from(event).hashCode());
    }

    @Override
    public boolean cachedExists(DomainEvent event) {
        Cache domainEventCache = lepCacheManager.getCache(DomainEvent.class.getSimpleName());
        if (domainEventCache == null) {
            log.info("Cache by name '{}' is not init", DomainEvent.class.getSimpleName());
            return false;
        }
        String hash = getHash(event);
        if (domainEventCache.get(hash, DomainEvent.class) == null) {
            domainEventCache.put(hash, event);
            return false;
        }
        return true;
    }
}
