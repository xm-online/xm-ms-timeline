package com.icthh.xm.ms.timeline.cucumber;

import org.junit.runner.RunWith;


import com.icthh.xm.ms.timeline.AbstractCassandraTest;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = "pretty", features = "src/test/features")
public class CucumberTest extends AbstractCassandraTest {

}
