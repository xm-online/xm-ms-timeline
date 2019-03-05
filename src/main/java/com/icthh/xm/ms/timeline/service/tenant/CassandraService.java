package com.icthh.xm.ms.timeline.service.tenant;

import com.builtamont.cassandra.migration.CassandraMigration;
import com.builtamont.cassandra.migration.api.configuration.ClusterConfiguration;
import com.builtamont.cassandra.migration.api.configuration.KeyspaceConfiguration;
import com.datastax.driver.core.Cluster;
import com.icthh.xm.commons.logging.aop.IgnoreLogginAspect;
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
@IgnoreLogginAspect
public class CassandraService {

    private final CassandraProperties cassandraProperties;
    private final ApplicationProperties properties;

    /**
     * Create cassandra keyspace.
     *
     * @param tenant the keyspace name
     */
    public void createCassandraKeyspace(String tenant) {
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            log.info("START - SETUP:CreateTenant:cassandra keyspace tenantKey: {}", tenant);
            Cluster.builder().addContactPoints(getContactPoints())
                .build().connect().execute(String.format(properties.getCassandra().getKeyspaceCreateCql(), tenant));
            log.info("STOP  - SETUP:CreateTenant:cassandra keyspace tenantKey: {}, result: OK, time = {} ms",
                tenant, stopWatch.getTime());
        } catch (Exception e) {
            log.error("STOP  - SETUP:CreateTenant:cassandra keyspace tenantKey: {}, result: FAIL,"
                    + " error: {}, time = {} ms",
                tenant, e.getMessage(), stopWatch.getTime());
            throw e;
        }
    }

    /**
     * Drop cassandra keyspace.
     *
     * @param tenant the keyspace name
     */
    public void dropCassandraKeyspace(String tenant) {
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            log.info("START - SETUP:DeleteTenant:cassandra keyspace tenantKey: {}", tenant);
            Cluster.builder().addContactPoints(getContactPoints())
                .build().connect().execute(String.format(Constants.CASSANDRA_DROP_KEYSPACE, tenant));
            log.info("STOP  - SETUP:DeleteTenant:cassandra keyspace tenantKey: {}, result: OK, time = {} ms",
                tenant, stopWatch.getTime());
        } catch (Exception e) {
            log.error("STOP  - SETUP:DeleteTenant:cassandra keyspace tenantKey: {},"
                    + " result: FAIL, error: {}, time = {} ms",
                tenant, e.getMessage(), stopWatch.getTime(), e);
        }
    }

    /**
     * Migrate cassandra keyspace.
     *
     * @param tenant the keyspace name
     */
    public void migrateCassandra(String tenant) {
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            log.info("START - SETUP:CreateTenant:cassandra migration tenantKey: {}", tenant);
            ClusterConfiguration clusterConfiguration = new ClusterConfiguration();
            clusterConfiguration.setContactpoints(getContactPoints());

            KeyspaceConfiguration keyspaceConfiguration = new KeyspaceConfiguration();
            keyspaceConfiguration.setName(tenant.toLowerCase());
            keyspaceConfiguration.setClusterConfig(clusterConfiguration);

            CassandraMigration cm = new CassandraMigration();
            cm.setLocations(new String[]{properties.getCassandra().getMigrationFolder()});
            cm.setKeyspaceConfig(keyspaceConfiguration);
            cm.migrate();
            log.info("STOP  - SETUP:CreateTenant:cassandra migration tenantKey: {}, result: OK, time = {} ms",
                tenant, stopWatch.getTime());
        } catch (Exception e) {
            log.error("STOP  - SETUP:CreateTenant:cassandra migration tenantKey: {},"
                    + " result: FAIL, error: {}, time = {} ms",
                tenant, e.getMessage(), stopWatch.getTime());
            throw e;
        }
    }

    private String[] getContactPoints() {
        return cassandraProperties.getContactPoints()
            .toArray(new String[cassandraProperties.getContactPoints().size()]);
    }
}
