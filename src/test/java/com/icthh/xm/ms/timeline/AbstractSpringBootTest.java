package com.icthh.xm.ms.timeline;

import com.icthh.xm.commons.migration.db.liquibase.LiquibaseRunner;
import com.icthh.xm.commons.migration.db.tenant.DropSchemaResolver;
import com.icthh.xm.ms.timeline.config.IntegrationTestConfiguration;
import com.icthh.xm.ms.timeline.config.TestLepConfiguration;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = {
        TestLepConfiguration.class,
        TimelineApp.class,
        IntegrationTestConfiguration.class,
        DropSchemaResolver.class,
        LiquibaseRunner.class
})
@Category(AbstractSpringBootTest.class)
@RunWith(SpringRunner.class)
public abstract class AbstractSpringBootTest {

}
