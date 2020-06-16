package io.terminus.dalaran.mapper;

import io.terminus.dalaran.mapper.context.DalaranFunctionContext;
import io.terminus.dalaran.mapper.context.DefaultDalaranFunctionContext;
import io.terminus.dalaran.mapper.spring.DalaranMapperLoader;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("io.terminus.dalaran")
public class DalaranMapperConfiguration {

    @Bean("mapper-loader")
    public BeanPostProcessor beanPostProcessor(DalaranFunctionContext dalaranFunctionContext) {
        return new DalaranMapperLoader(dalaranFunctionContext);
    }

    @Bean
    public DalaranFunctionContext dalaranFunctionContext() {
        return new DefaultDalaranFunctionContext();
    }
}
