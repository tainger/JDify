package io.terminus.dalaran.mapper;

import io.terminus.dalaran.mapper.context.DalaranFunctionContext;
import io.terminus.dalaran.mapper.context.DefaultDalaranFunctionContext;
import io.terminus.dalaran.mapper.spring.DalaranMapperLoader;
import io.terminus.dalaran.model.annotation.IgnoreScan;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "io.terminus.dalaran", excludeFilters = @ComponentScan.Filter(IgnoreScan.class))
public class DalaranMapperConfiguration {

    @Bean("mapper-loader")
    public BeanPostProcessor beanPostProcessor(DalaranFunctionContext dalaranFunctionContext) {
        return new DalaranMapperLoader(dalaranFunctionContext);
    }

    @Bean("function-context")
    @ConditionalOnMissingBean
    public DalaranFunctionContext dalaranFunctionContext() {
        return new DefaultDalaranFunctionContext();
    }
}
