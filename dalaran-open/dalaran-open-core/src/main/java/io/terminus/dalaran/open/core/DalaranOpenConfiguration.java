package io.terminus.dalaran.open.core;


import io.terminus.dalaran.open.common.service.DalaranOpenServiceContext;
import io.terminus.dalaran.open.core.context.DefaultDalaranOpenServiceContext;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "io.terminus")
public class DalaranOpenConfiguration {

    @Bean
    public DalaranOpenServiceContext dalaranOpenServiceContext() {
        return new DefaultDalaranOpenServiceContext();
    }

    @Bean("open-service-loader")
    public BeanPostProcessor beanPostProcessor(DalaranOpenServiceContext dalaranOpenServiceContext) {
        return new DalaranOpenServiceLoader(dalaranOpenServiceContext);
    }
}
