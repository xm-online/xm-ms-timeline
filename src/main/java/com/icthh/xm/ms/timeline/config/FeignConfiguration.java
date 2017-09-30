package com.icthh.xm.ms.timeline.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.icthh.xm.ms.timeline")
public class FeignConfiguration {

}
