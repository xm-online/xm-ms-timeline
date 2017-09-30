package com.icthh.xm.ms.timeline.web.rest;

import com.icthh.xm.commons.gen.api.TenantsApiDelegate;
import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.ms.timeline.service.tenant.TenantService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TenantResource implements TenantsApiDelegate {

    private final TenantService service;

    @Override
    public ResponseEntity<Void> addTenant(Tenant tenant) {
        service.createTenant(tenant.getTenantKey());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteTenant(String tenantKey) {
        service.deleteTenant(tenantKey);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<Tenant>> getAllTenantInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResponseEntity<Tenant> getTenant(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResponseEntity<Void> manageTenant(String tenant, String state) {
        service.manageTenant(tenant, state);
        return ResponseEntity.ok().build();
    }
}
