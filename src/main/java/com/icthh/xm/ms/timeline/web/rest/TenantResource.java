package com.icthh.xm.ms.timeline.web.rest;

import com.icthh.xm.commons.gen.api.TenantsApiDelegate;
import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.ms.timeline.service.tenant.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TenantResource implements TenantsApiDelegate {

    private final TenantService service;

    @Override
    @PreAuthorize("hasPermission({'tenant':#tenant}, 'TIMELINE.TENANT.CREATE')")
    public ResponseEntity<Void> addTenant(Tenant tenant) {
        service.createTenant(tenant.getTenantKey());
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasPermission({'tenantKey':#tenantKey}, 'TIMELINE.TENANT.DELETE')")
    public ResponseEntity<Void> deleteTenant(String tenantKey) {
        service.deleteTenant(tenantKey);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostAuthorize("hasPermission(null, 'TIMELINE.TENANT.GET_LIST')")
    public ResponseEntity<List<Tenant>> getAllTenantInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    @PostAuthorize("hasPermission({'returnObject': returnObject}, 'TIMELINE.TENANT.GET_LIST.ITEM')")
    public ResponseEntity<Tenant> getTenant(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    @PreAuthorize("hasPermission({'tenant':#tenant, 'status':#status}, 'TIMELINE.TENANT.UPDATE')")
    public ResponseEntity<Void> manageTenant(String tenant, String status) {
        service.manageTenant(tenant, status);
        return ResponseEntity.ok().build();
    }
}
