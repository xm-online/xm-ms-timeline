package com.icthh.xm.ms.timeline.service.tenant.provisioner;

import static com.icthh.xm.commons.tenant.TenantContextUtils.assertTenantKeyValid;

import com.builtamont.cassandra.migration.CassandraMigration;
import com.builtamont.cassandra.migration.api.configuration.ClusterConfiguration;
import com.builtamont.cassandra.migration.api.configuration.KeyspaceConfiguration;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.SocketOptions;
import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantProvisioner;
import com.icthh.xm.ms.timeline.config.ApplicationProperties;
import com.icthh.xm.ms.timeline.config.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TenantCassandraStorageProvisioner implements TenantProvisioner {

    private final CassandraProperties cassandraProperties;
    private final ApplicationProperties applicationProperties;

    @Override
    public void createTenant(final Tenant tenant) {
        String tenantKeyFormatted = formatTenantKey(tenant.getTenantKey());
        assertTenantKeyValid(tenantKeyFormatted);
        createCassandraKeyspace(tenantKeyFormatted);
        migrateCassandra(tenantKeyFormatted);
    }

    @Override
    public void manageTenant(final String tenantKey, final String state) {
        log.info("Nothing to do with Cassandra storage during manage tenant: {}, state = {}", tenantKey, state);
    }

    @Override
    public void deleteTenant(final String tenantKey) {
        String tenantKeyFormatted = formatTenantKey(tenantKey);
        assertTenantKeyValid(tenantKeyFormatted);
        dropCassandraKeyspace(tenantKeyFormatted);
    }

    /**
     * Create cassandra keyspace.
     *
     * @param tenant the keyspace name
     */
    private void createCassandraKeyspace(String tenant) {
        StopWatch stopWatch = StopWatch.createStarted();

        Cluster.builder()
               .addContactPoints(getContactPoints())
               .build()
               .connect()
               .execute(String.format(applicationProperties.getCassandra().getKeyspaceCreateCql(), tenant));
        log.info("Cassandra keyspace created for tenantKey: {}, time = {} ms", tenant, stopWatch.getTime());
    }

    /**
     * Drop cassandra keyspace.
     *
     * @param tenant the keyspace name
     */
    private void dropCassandraKeyspace(String tenant) {
        StopWatch stopWatch = StopWatch.createStarted();
        Cluster.builder()
               .withSocketOptions(new SocketOptions().setReadTimeoutMillis(60 * 1000))
               .addContactPoints(getContactPoints())
               .build()
               .connect()
               .execute(String.format(Constants.CASSANDRA_DROP_KEYSPACE, tenant));
        log.info("Cassandra keyspace dropped for tenantKey: {}, time = {} ms", tenant, stopWatch.getTime());
    }

    /**
     * Migrate cassandra keyspace.
     *
     * @param tenant the keyspace name
     */
    private void migrateCassandra(String tenant) {
        StopWatch stopWatch = StopWatch.createStarted();
        ClusterConfiguration clusterConfiguration = new ClusterConfiguration();
        clusterConfiguration.setContactpoints(getContactPoints());

        KeyspaceConfiguration keyspaceConfiguration = new KeyspaceConfiguration();
        keyspaceConfiguration.setName(tenant.toLowerCase());
        keyspaceConfiguration.setClusterConfig(clusterConfiguration);

        CassandraMigration cm = new CassandraMigration();
        cm.setLocations(new String[]{applicationProperties.getCassandra().getMigrationFolder()});
        cm.setKeyspaceConfig(keyspaceConfiguration);
        cm.migrate();
        log.info("Cassandra keyspace migrated for tenantKey: {}, time = {} ms", tenant, stopWatch.getTime());
    }

    private String[] getContactPoints() {
        return cassandraProperties.getContactPoints().toArray(new String[0]);
    }

    private String formatTenantKey(String tenantKey) {
        return tenantKey.toLowerCase();
    }

}
