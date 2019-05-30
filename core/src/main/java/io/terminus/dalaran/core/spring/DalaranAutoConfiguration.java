package io.terminus.dalaran.core.spring;

import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.context.DalaranConverterContext;
import io.terminus.dalaran.core.context.DalaranServiceContext;
import io.terminus.dalaran.core.context.support.DefaultDalaranCamelContext;
import io.terminus.dalaran.core.context.support.DefaultDalaranComponentContext;
import io.terminus.dalaran.core.context.support.DefaultDalaranConverterContext;
import io.terminus.dalaran.core.context.support.DefaultDalaranServiceContext;
import io.terminus.dalaran.core.flow.DalaranFlowBuilder;
import io.terminus.dalaran.core.flow.DefaultCamelFlowBuilder;
import io.terminus.dalaran.core.log.DalaranTraceLogger;
import io.terminus.dalaran.core.log.TracingErrorHandlerFactory;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.DalaranResourceLoader;
import io.terminus.dalaran.core.resource.DefaultDalaranResourceBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.Registry;
import org.apache.camel.spring.spi.ApplicationContextRegistry;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DalaranAutoConfiguration {

    @Bean
    public CamelContext camelContext(ApplicationContext applicationContext) {
        Registry registry = new ApplicationContextRegistry(applicationContext);
        return new DefaultCamelContext(registry);
    }

    @Bean
    public DalaranContext dalaranContext(
            DalaranConverterContext converterContext,
            DalaranComponentContext componentContext,
            DalaranServiceContext serviceContext,
            DalaranFlowBuilder flowBuilder,
            CamelContext camelContext
    ) {
        return new DefaultDalaranCamelContext(camelContext, converterContext, componentContext, serviceContext, flowBuilder);
    }

    @Bean
    public DalaranResourceBuilder dalaranResourceBuilder(
            DalaranResourceLoader resourceLoader,
            DalaranComponentContext componentContext,
            DalaranConverterContext converterContext
    ) {
        return new DefaultDalaranResourceBuilder(resourceLoader, componentContext, converterContext);
    }

    @Bean("component-loader")
    public BeanPostProcessor beanPostProcessor(DalaranComponentContext dalaranComponentContext) {
        return new DalaranComponentLoader(dalaranComponentContext);
    }

    @Bean
    public DalaranComponentContext dalaranComponentContext() {
        return new DefaultDalaranComponentContext();
    }

    @Bean
    public DalaranConverterContext dalaranConverterContext() {
        return new DefaultDalaranConverterContext();
    }

    @Bean
    public DalaranServiceContext dalaranServiceContext() {
        return new DefaultDalaranServiceContext();
    }

    @Bean
    public TracingErrorHandlerFactory tracingErrorHandlerFactory(DalaranTraceLogger traceLogger) {
        return new TracingErrorHandlerFactory(traceLogger);
    }

    @Bean
    public DalaranFlowBuilder flowBuilder(
            DalaranTraceLogger traceLogger,
            TracingErrorHandlerFactory errorHandlerFactory,
            DalaranConverterContext converterContext,
            DalaranComponentContext componentContext
    ) {
        return new DefaultCamelFlowBuilder(traceLogger, errorHandlerFactory, converterContext, componentContext);
    }
}
