package com.icthh.xm.ms.timeline.config;

import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.cloud.client.loadbalancer.RetryLoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RetryInterceptorConfiguration extends LoadBalancerAutoConfiguration.RetryInterceptorAutoConfiguration {

    @Bean
    @Profile("nolb")
    public RestTemplateCustomizer restTemplateCustomizer(
        final RetryLoadBalancerInterceptor loadBalancerInterceptor) {
        return restTemplate -> {
            List<ClientHttpRequestInterceptor> list = new ArrayList<>(
                restTemplate.getInterceptors());
            restTemplate.setInterceptors(list);
        };
    }

}
