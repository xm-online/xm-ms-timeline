package com.icthh.xm.ms.timeline.service.tenant.provisioner;

import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantProvisioner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TenantLoggerStorageProvisioner implements TenantProvisioner {
    @Override
    public void createTenant(final Tenant tenant) {
        log.info("Nothing to do with Logger storage create manage tenant: {}", tenant.getTenantKey());
    }

    @Override
    public void manageTenant(final String tenantKey, final String state) {
        log.info("Nothing to do with Logger storage during manage tenant: {}, state = {}", tenantKey, state);
    }

    @Override
    public void deleteTenant(final String tenantKey) {
        log.info("Nothing to do with Logger storage during delete tenant: {}}", tenantKey);
    }
}
