package com.icthh.xm.ms.timeline.service.cache;

import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Slf4j
public class TenantCacheManagerFacade implements ApplicationContextAware {

    @Value("${application.deduplication.strategy}")
    private String deduplicationStrategyName;

    private static ApplicationContext context;

    public boolean skipDuplicatedDomainEvent(DomainEvent domainEvent) {
        EventDeduplicationStrategy strategy = getStrategy();
        return strategy != null && strategy.cachedExists(domainEvent);
    }

    private EventDeduplicationStrategy getStrategy() {
        try {
            return context.getBean(deduplicationStrategyName, EventDeduplicationStrategy.class);
        } catch (NoSuchBeanDefinitionException e) {
            log.error("Cache deduplication strategy by name '{}' not found", deduplicationStrategyName);
            return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
