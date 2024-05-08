package com.icthh.xm.ms.timeline.lep.keyresolver;

import com.icthh.xm.commons.domainevent.domain.DomainEvent;
import com.icthh.xm.lep.api.LepKeyResolver;
import com.icthh.xm.lep.api.LepMethod;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DomainEventAggregateTypeResolver implements LepKeyResolver {

    @Override
    public List<String> segments(LepMethod method) {
        return Arrays.stream(method.getMethodArgValues())
            .filter(arg -> arg instanceof DomainEvent)
            .map(arg -> ((DomainEvent) arg).getAggregateType())
            .collect(Collectors.toList());
    }

}
