package com.icthh.xm.ms.timeline;

import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.icthh.xm.ms.timeline.config.SecurityBeanOverrideConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TimelineApp.class, SecurityBeanOverrideConfiguration.class})
public class CassandraKeyspaceUnitTest extends AbstractCassandraTest {

    @Autowired
    private Session session;

    @Test
    public void shouldListCassandraUnitKeyspace() {
        Metadata metadata = session.getCluster().getMetadata();
        assertThat(metadata.getKeyspace(CASSANDRA_UNIT_KEYSPACE)).isNotNull();
    }
}
