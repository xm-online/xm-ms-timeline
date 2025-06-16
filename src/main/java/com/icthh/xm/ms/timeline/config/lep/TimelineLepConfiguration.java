package com.icthh.xm.ms.timeline.config.lep;

import com.icthh.xm.commons.lep.groovy.GroovyLepEngineConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimelineLepConfiguration extends GroovyLepEngineConfiguration {
    public TimelineLepConfiguration(@Value("${spring.application.name}") String appName) {
        super(appName);
    }
}
