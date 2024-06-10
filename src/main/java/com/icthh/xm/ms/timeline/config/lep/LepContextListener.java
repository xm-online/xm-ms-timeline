package com.icthh.xm.ms.timeline.config.lep;

import com.icthh.xm.commons.config.client.api.ConfigService;
import com.icthh.xm.commons.config.client.service.TenantConfigService;
import com.icthh.xm.commons.lep.api.BaseLepContext;
import com.icthh.xm.commons.lep.api.LepContextFactory;
import com.icthh.xm.lep.api.LepMethod;
import com.icthh.xm.ms.timeline.service.cache.TenantCacheManagerFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LepContextListener implements LepContextFactory {

    private final TenantConfigService tenantConfigService;
    private final ConfigService configService;
    private final Optional<TenantCacheManagerFacade> tenantCacheManagerFacade;

    @Override
    public BaseLepContext buildLepContext(LepMethod lepMethod) {
        LepContext lepContext = new LepContext();
        lepContext.tenantConfigService = tenantConfigService;
        lepContext.configService = configService;
        tenantCacheManagerFacade.ifPresent(facade -> lepContext.tenantCacheManagerFacade = facade);
        return lepContext;
    }
}
