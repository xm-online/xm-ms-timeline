package com.icthh.xm.ms.timeline.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.icthh.xm.commons.config.client.api.RefreshableConfiguration;
import com.icthh.xm.commons.config.client.repository.TenantConfigRepository;
import com.icthh.xm.commons.logging.aop.IgnoreLogginAspect;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.ms.timeline.config.ApplicationProperties;
import com.icthh.xm.ms.timeline.domain.properties.TenantProperties;

import java.util.concurrent.ConcurrentHashMap;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantPropertiesService implements RefreshableConfiguration {

    private static final String TENANT_NAME = "tenantName";

    private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    private ConcurrentHashMap<String, TenantProperties> tenantProps = new ConcurrentHashMap<>();

    private final AntPathMatcher matcher = new AntPathMatcher();

    private final ApplicationProperties applicationProperties;

    private final TenantConfigRepository tenantConfigRepository;

    private final TenantContextHolder tenantContextHolder;

    @IgnoreLogginAspect
    public TenantProperties getTenantProps() {
        String tenant = TenantContextUtils.getRequiredTenantKeyValue(tenantContextHolder);
        if (!tenantProps.containsKey(tenant)) {
            throw new IllegalArgumentException("Tenant configuration not found for tenant: " + tenant);
        }
        return tenantProps.get(tenant);
    }

    @SneakyThrows
    public void updateTenantProps(String timelineYml) {
        String tenant = TenantContextUtils.getRequiredTenantKeyValue(tenantContextHolder);
        String configName = applicationProperties.getTenantPropertiesName();

        // Simple validation correct structure
        TenantProperties tenantProperties = mapper.readValue(timelineYml, TenantProperties.class);

        tenantConfigRepository.updateConfig(tenant, "/" + configName, timelineYml);
    }

    @Override
    @SneakyThrows
    public void onRefresh(String updatedKey, String config) {
        String specificationPathPattern = applicationProperties.getTenantPropertiesPathPattern();
        try {
            String tenant = matcher.extractUriTemplateVariables(specificationPathPattern, updatedKey).get(TENANT_NAME);
            if (StringUtils.isBlank(config)) {
                tenantProps.remove(tenant);
                return;
            }
            TenantProperties spec = mapper.readValue(config, TenantProperties.class);
            tenantProps.put(tenant, spec);
            log.info("Specification was for tenant {} updated", tenant);
        } catch (Exception e) {
            log.error("Error read xm specification from path " + updatedKey, e);
        }
    }

    @Override
    public boolean isListeningConfiguration(String updatedKey) {
        String specificationPathPattern = applicationProperties.getTenantPropertiesPathPattern();
        return matcher.match(specificationPathPattern, updatedKey);
    }

    @Override
    public void onInit(String key, String config) {
        if (isListeningConfiguration(key)) {
            onRefresh(key, config);
        }
    }
}
