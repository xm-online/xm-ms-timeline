package com.icthh.xm.ms.timeline.lep.keyresolver;

import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import com.icthh.xm.commons.lep.AppendLepKeyResolver;
import com.icthh.xm.lep.api.LepManagerService;
import com.icthh.xm.lep.api.LepMethod;
import com.icthh.xm.lep.api.commons.SeparatorSegmentedLepKey;
import org.springframework.stereotype.Component;

@Component
public class DomainEventAggregateTypeResolver extends AppendLepKeyResolver {

    @Override
    protected String[] getAppendSegments(SeparatorSegmentedLepKey baseKey, LepMethod method, LepManagerService managerService) {
        DomainEvent domainEvent = getRequiredParam(method, "domainEvent", DomainEvent.class);
        String translateToLepConvention = translateToLepConvention(domainEvent.getAggregateType());
        return new String[]{translateToLepConvention};
    }
}
