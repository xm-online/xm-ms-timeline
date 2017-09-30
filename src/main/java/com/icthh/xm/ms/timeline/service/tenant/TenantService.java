package com.icthh.xm.ms.timeline.service.tenant;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.icthh.xm.commons.config.client.repository.TenantConfigRepository;
import com.icthh.xm.commons.config.client.repository.TenantListRepository;
import com.icthh.xm.commons.logging.aop.IgnoreLogginAspect;
import com.icthh.xm.ms.timeline.config.ApplicationProperties;
import com.icthh.xm.ms.timeline.config.Constants;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
@IgnoreLogginAspect
public class TenantService {

    private final CassandraService cassandraService;
    private final KafkaService kafkaService;
    private final TenantListRepository tenantListRepository;
    private final TenantConfigRepository tenantConfigRepository;
    private final ApplicationProperties applicationProperties;

    /**
     * Create new tenant.
     * @param tenant the new tenant name
     */
    public void createTenant(String tenant) {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("START - SETUP:CreateTenant: tenantKey: {}", tenant);

        try {
            tenantListRepository.addTenant(tenant.toLowerCase());
            cassandraService.createCassandraKeyspace(tenant);
            cassandraService.migrateCassandra(tenant);
            kafkaService.createKafkaTopic(tenant);
            kafkaService.sendCommand(tenant, Constants.CREATE_COMMAND);
            addTimelineSpecification(tenant.toUpperCase());

            log.info("STOP  - SETUP:CreateTenant: tenantKey: {}, result: OK, time = {} ms",
                tenant, stopWatch.getTime());
        } catch (Exception e) {
            log.info("STOP  - SETUP:CreateTenant: tenantKey: {}, result: FAIL, error: {}, time = {} ms",
                tenant, e.getMessage(), stopWatch.getTime());
            throw e;
        }
    }

    /**
     * Delete tenant.
     * @param tenant the tenant name
     */
    public void deleteTenant(String tenant) {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("START - SETUP:DeleteTenant: tenantKey: {}", tenant);

        try {
            cassandraService.dropCassandraKeyspace(tenant);
            kafkaService.deleteKafkaTopic(tenant);
            kafkaService.sendCommand(tenant, Constants.DELETE_COMMAND);
            tenantListRepository.deleteTenant(tenant.toLowerCase());

            String specificationName = applicationProperties.getTenantPropertiesName();
            tenantConfigRepository.deleteConfig(tenant.toUpperCase(), "/" + specificationName);

            log.info("STOP  - SETUP:DeleteTenant: tenantKey: {}, result: OK, time = {} ms",
                tenant, stopWatch.getTime());
        } catch (Exception e) {
            log.info("STOP  - SETUP:DeleteTenant: tenantKey: {}, result: FAIL, error: {}, time = {} ms",
                tenant, e.getMessage(), stopWatch.getTime());
            throw e;
        }
    }

    public void manageTenant(String tenant, String state) {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("START - SETUP:ManageTenant: tenantKey: {}, state: {}", tenant, state);

        try {
            tenantListRepository.updateTenant(tenant.toLowerCase(), state.toUpperCase());

            log.info("STOP  - SETUP:ManageTenant: tenantKey: {}, state: {}, result: OK, time = {} ms",
                tenant, state, stopWatch.getTime());
        } catch (Exception e) {
            log.info("STOP  - SETUP:ManageTenant: tenantKey: {}, state: {}, result: FAIL, error: {}, time = {} ms",
                tenant, state, e.getMessage(), stopWatch.getTime());
            throw e;
        }
    }

    @SneakyThrows
    private void addTimelineSpecification(String tenantName) {
        String specificationName = applicationProperties.getTenantPropertiesName();
        InputStream in = new ClassPathResource(Constants.DEFAULT_CONFIG_PATH).getInputStream();
        String specification = IOUtils.toString(in, UTF_8);
        tenantConfigRepository.updateConfig(tenantName, "/" + specificationName, specification);
    }
}
