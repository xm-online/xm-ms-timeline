package com.icthh.xm.ms.timeline.config;

import com.icthh.xm.commons.cache.TenantCacheManager;
import com.icthh.xm.ms.timeline.service.cache.EventDeduplicationStrategy;
import com.icthh.xm.ms.timeline.service.cache.MemoryLepDeduplicationStrategy;
import com.icthh.xm.ms.timeline.service.cache.EventDeduplicationStrategyFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "application.tenant-memory-cache.enabled", havingValue = "true")
public class TenantCacheConfiguration {

    @Bean
    public EventDeduplicationStrategyFactory tenantCacheManagerFacade() {
        return new EventDeduplicationStrategyFactory();
    }

    @Bean
    public EventDeduplicationStrategy memoryLepDeduplicationStrategy(TenantCacheManager lepCacheManager) {
        return new MemoryLepDeduplicationStrategy(lepCacheManager);
    }
}
