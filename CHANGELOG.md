# [3.1.5](https://github.com/xm-online/xm-ms-timeline/compare/master...feature/DA-69-event-deduplication-filtering) (2024-07-01)


### Bug Fixes

### Code Refactoring

### Features

* Caching in memory filter for duplication event filtering. Filter is enabled by [application-cache.yml](src%2Fmain%2Fresources%2Fconfig%2Fapplication-cache.yml)
```.groovy
@Slf4j
@Transactional
class InsertTimelineService {

    private TenantCacheManagerFacade tenantCacheManagerFacade
    ...
    
    public InsertTimelineService(LepContext lepContext) {
        this.tenantCacheManagerFacade = lepContext.tenantCacheManagerFacade
        ...
    }

    public void insertTimeLine(DomainEvent domainEvent) {
        if (tenantCacheManagerFacade != null && tenantCacheManagerFacade.skipDuplicatedDomainEvent(domainEvent)) {
            log.info("Skip domain event as a duplicate by id: {}", domainEvent.id)
            return
        }
        ...
    }

}
```

