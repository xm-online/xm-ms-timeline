package com.icthh.xm.ms.timeline;

import com.icthh.xm.commons.migration.db.liquibase.LiquibaseRunner;
import com.icthh.xm.commons.migration.db.tenant.DropSchemaResolver;
import com.icthh.xm.ms.timeline.config.IntegrationTestConfiguration;
import com.icthh.xm.ms.timeline.config.TestLepConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = {
        TestLepConfiguration.class,
        TimelineApp.class,
        IntegrationTestConfiguration.class,
        DropSchemaResolver.class,
        LiquibaseRunner.class
})
@ExtendWith(SpringExtension.class)
public abstract class AbstractSpringBootTest {

}
