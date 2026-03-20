package com.icthh.xm.ms.timeline.config;

import com.icthh.xm.commons.security.jwt.TokenProvider;
import com.icthh.xm.commons.security.spring.config.SecurityConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimelineSecurityConfiguration extends SecurityConfiguration {

    public TimelineSecurityConfiguration(TokenProvider tokenProvider,
                                         @Value("${jhipster.security.content-security-policy}")
                                         String contentSecurityPolicy) {
        super(tokenProvider, contentSecurityPolicy);
    }
}
