package com.icthh.xm.ms.timeline.config;

import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.cloud.client.loadbalancer.RetryLoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;

@Profile("nolb")
@Configuration
public class RetryInterceptorConfiguration extends LoadBalancerAutoConfiguration.RetryInterceptorAutoConfiguration {

    @Bean
    @Override
    public RestTemplateCustomizer restTemplateCustomizer(final RetryLoadBalancerInterceptor loadBalancerInterceptor) {
        return restTemplate -> {
            var list = new ArrayList<>(restTemplate.getInterceptors());
            restTemplate.setInterceptors(list);
        };
    }

}
