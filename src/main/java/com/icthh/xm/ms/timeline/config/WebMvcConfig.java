package com.icthh.xm.ms.timeline.config;

import com.icthh.xm.commons.web.spring.TenantInterceptor;
import com.icthh.xm.commons.web.spring.TenantVerifyInterceptor;
import com.icthh.xm.commons.web.spring.XmLoggingInterceptor;
import com.icthh.xm.commons.web.spring.config.XmMsWebConfiguration;
import com.icthh.xm.commons.web.spring.config.XmWebMvcConfigurerAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;

import java.util.List;

@Configuration
@Import(XmMsWebConfiguration.class)
public class WebMvcConfig extends XmWebMvcConfigurerAdapter {

    private final ApplicationProperties applicationProperties;
    private final TenantVerifyInterceptor tenantVerifyInterceptor;

    protected WebMvcConfig(
                    TenantInterceptor tenantInterceptor,
                    XmLoggingInterceptor xmLoggingInterceptor,
                    ApplicationProperties applicationProperties,
                    TenantVerifyInterceptor tenantVerifyInterceptor) {
        super(tenantInterceptor, xmLoggingInterceptor);
        this.applicationProperties = applicationProperties;
        this.tenantVerifyInterceptor = tenantVerifyInterceptor;
    }

    @Override
    protected void xmAddInterceptors(InterceptorRegistry registry) {
        registerTenantInterceptorWithIgnorePathPattern(registry, tenantVerifyInterceptor);
    }

    @Override
    protected void xmConfigurePathMatch(PathMatchConfigurer configurer) {

    }

    @Override
    protected List<String> getTenantIgnorePathPatterns() {
        return applicationProperties.getTenantIgnoredPathList();
    }

}
