package com.icthh.xm.ms.timeline.lep.keyresolver;

import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import com.icthh.xm.lep.api.LepKeyResolver;
import com.icthh.xm.lep.api.LepMethod;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DomainEventAggregateTypeResolver implements LepKeyResolver {

    @Override
    public List<String> segments(LepMethod method) {
        return List.of(
            method.getParameter("domainEvent", DomainEvent.class).getAggregateType()
        );
    }

}
