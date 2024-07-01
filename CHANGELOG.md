# [3.1.5](https://github.com/xm-online/xm-ms-timeline/compare/master...feature/DA-69-event-deduplication-filtering) (2024-07-01)


### Bug Fixes

### Code Refactoring

### Features

* Caching in memory filter for duplication event filtering. Filter is enabled by [application-cache.yml](src%2Fmain%2Fresources%2Fconfig%2Fapplication-cache.yml)
```.groovy
@Slf4j
@Transactional
class InsertTimelineService {

    private EventDeduplicationStrategyFactory eventDeduplicationStrategyFactory
    private EventDeduplicationStrategy eventDeduplicationStrategy

    public InsertTimelineService(LepContext lepContext) {
        this.eventDeduplicationStrategyFactory = lepContext.eventDeduplicationStrategyFactory
        if (eventDeduplicationStrategyFactory != null) {
            eventDeduplicationStrategy = eventDeduplicationStrategyFactory.getStrategy()
        }
        ...
    }

    public void insertTimeLine(DomainEvent domainEvent) {
        if (skipDuplicatedDomainEvent(domainEvent)) {
            log.info("Skip domain event as a duplicate by id: {}", domainEvent.id)
            return
        }  
        ...      
    }

    boolean skipDuplicatedDomainEvent(DomainEvent domainEvent) {
        return eventDeduplicationStrategy != null && eventDeduplicationStrategy.cachedExists(domainEvent);
    }

}    
```

