package com.icthh.xm.ms.timeline.config.lep;


import com.icthh.xm.commons.config.client.api.ConfigService;
import com.icthh.xm.commons.config.client.service.TenantConfigService;
import com.icthh.xm.commons.lep.api.BaseLepContext;
import com.icthh.xm.commons.logging.trace.TraceService.TraceServiceField;
import com.icthh.xm.ms.timeline.service.cache.EventDeduplicationStrategyFactory;

public class LepContext extends BaseLepContext implements TraceServiceField {

    public TenantConfigService tenantConfigService;
    public ConfigService configService;
    public EventDeduplicationStrategyFactory eventDeduplicationStrategyFactory;

}

