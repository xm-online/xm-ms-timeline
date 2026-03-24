package com.icthh.xm.ms.timeline.config;

import com.icthh.xm.commons.security.jwt.TokenProvider;
import com.icthh.xm.commons.security.spring.config.SecurityConfiguration;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@ConditionalOnMissingBean(name = "timelineSecurityConfiguration")
public class TimelineSecurityConfig extends SecurityConfiguration {

    public TimelineSecurityConfig(TokenProvider tokenProvider,
                                  @Value("${jhipster.security.content-security-policy}")
                                         String contentSecurityPolicy) {
        super(tokenProvider, contentSecurityPolicy);
    }

    @Override
    @SneakyThrows
    protected HttpSecurity applyUrlSecurity(HttpSecurity http) {
        http
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/management/consumer-status/**").authenticated()
                );
        return super.applyUrlSecurity(http);
    }
}
