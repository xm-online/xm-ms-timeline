package com.icthh.xm.ms.timeline.service.cache;

import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class DomainEventHashCodeWrapper {

    private String txId;
    private String aggregateId;
    private String aggregateType;
    private String aggregateName;
    private String operation;
    private String msName;
    private String source;
    private String userKey;
    private String clientId;
    private String tenant;

    public static DomainEventHashCodeWrapper from(DomainEvent domainEvent) {
        DomainEventHashCodeWrapper wrapper = new DomainEventHashCodeWrapper();
        wrapper.txId = domainEvent.getTxId();
        wrapper.aggregateId = domainEvent.getAggregateId();
        wrapper.aggregateType = domainEvent.getAggregateType();
        wrapper.aggregateName = domainEvent.getAggregateName();
        wrapper.operation = domainEvent.getOperation();
        wrapper.msName = domainEvent.getMsName();
        wrapper.source = domainEvent.getSource();
        wrapper.userKey = domainEvent.getUserKey();
        wrapper.clientId = domainEvent.getClientId();
        wrapper.tenant = domainEvent.getTenant();
        return wrapper;
    }
}
