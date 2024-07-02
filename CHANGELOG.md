# [3.1.5](https://github.com/xm-online/xm-ms-timeline/compare/master...feature/DA-69-event-deduplication-filtering) (2024-07-01)


### Bug Fixes

### Code Refactoring

### Features

* Caching in memory filter for duplication event filtering. Filter is enabled by [application-cache.yml](src%2Fmain%2Fresources%2Fconfig%2Fapplication-cache.yml)
* Example of building LEP event filter using InMemory cache
  * lep ProcessEvent$$saveDomainEvent$$around.groovy calls  InsertTimelineService
  * InsertTimelineService receives currently available eventDeduplicationStrategy (local im memory realized, but could be extended to shared, ect)
  * MemoryLepDeduplicationStrategy uses tenantCacheStrategy to work with configured memory cache for this feature 
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
* Cache configuration example
```yaml
- cacheName: DomainEvent
  expireAfterWrite: 3
  maximumSize: 1000
  recordStats: true
```

