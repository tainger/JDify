package io.terminus.dalaran.core.spring;

import io.terminus.dalaran.core.component.config.CustomCamelContext;
import io.terminus.dalaran.core.context.*;
import io.terminus.dalaran.core.context.support.*;
import io.terminus.dalaran.core.flow.DalaranFlowBuilder;
import io.terminus.dalaran.core.flow.DefaultCamelFlowBuilder;
import io.terminus.dalaran.core.log.DalaranTraceLogger;
import io.terminus.dalaran.core.log.TracingErrorHandlerFactory;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.DalaranResourceLoader;
import io.terminus.dalaran.core.resource.DefaultDalaranResourceBuilder;
import io.terminus.dalaran.core.resource.oss.OSSAccount;
import io.terminus.dalaran.core.resource.redis.RedisService;
import org.apache.camel.CamelContext;
import org.apache.camel.spi.Registry;
import org.apache.camel.spring.spi.ApplicationContextRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DalaranAutoConfiguration {

    @Bean
    public CamelContext camelContext(ApplicationContext applicationContext, @Value("${terminus.dalaran.tracing:true}") boolean tracing) {
        Registry registry = new ApplicationContextRegistry(applicationContext);
        CamelContext camelContext = new CustomCamelContext(registry);
        try {
            camelContext.setTracing(tracing);
            camelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camelContext;
    }

    @Bean
    public DalaranContext dalaranContext(
            DalaranFlowBuilder flowBuilder,
            CamelContext camelContext
    ) {
        return new DefaultDalaranCamelContext(camelContext, flowBuilder);
    }

    @Bean
    public DalaranResourceBuilder dalaranResourceBuilder(
            DalaranResourceLoader resourceLoader,
            DalaranComponentContext componentContext,
            DalaranModelTypeContext converterContext,
            DalaranServiceContext serviceContext
    ) {
        return new DefaultDalaranResourceBuilder(resourceLoader, componentContext, converterContext, serviceContext);
    }

    @Bean("component-loader")
    public BeanPostProcessor beanPostProcessor(
            DalaranComponentContext dalaranComponentContext,
            DalaranFunctionContext dalaranFunctionContext,
            DalaranModelTypeContext dalaranModelTypeContext
    ) {
        return new DalaranComponentLoader(dalaranComponentContext, dalaranFunctionContext, dalaranModelTypeContext);
    }

    @Bean
    public DalaranComponentContext dalaranComponentContext() {
        return new DefaultDalaranComponentContext();
    }


    @Bean
    public DalaranClientContext dalaranClientContext() {
        return new DefaultDalaranClientContext();
    }

    @Bean
    public DalaranFunctionContext dalaranFunctionContext() {
        return new DefaultDalaranFunctionContext();
    }

    @Bean
    public DalaranModelTypeContext dalaranConverterContext() {
        return new DefaultDalaranModelTypeContext();
    }

    @Bean
    public DalaranServiceContext dalaranServiceContext() {
        return new DefaultDalaranServiceContext();
    }

    @Bean
    public TracingErrorHandlerFactory tracingErrorHandlerFactory(DalaranTraceLogger traceLogger, RedisService redisService) {
        return new TracingErrorHandlerFactory(traceLogger, redisService);
    }

    @Bean
    public DalaranFlowBuilder flowBuilder(
            DalaranTraceLogger traceLogger,
            TracingErrorHandlerFactory errorHandlerFactory,
            DalaranModelTypeContext converterContext,
            DalaranComponentContext componentContext
    ) {
        return new DefaultCamelFlowBuilder(traceLogger, errorHandlerFactory, converterContext, componentContext);
    }

    @Bean
    public OSSAccount ossAccount(@Value("${oss.endpoint}") String endpoint, @Value("${oss.accessId}") String accessId, @Value("${oss.accessSecret}") String accessSecret, @Value("${oss.bucketName}") String bucketName, @Value("${oss.rootDir}") String rootDir) {
        return new OSSAccount(endpoint, accessId, accessSecret, bucketName, rootDir);
    }

//    @Bean
//    public HttpComponent httpComponent() {
//        HttpComponent component = new HttpComponent();
//        component.setConnectionTimeToLive(0);
//        return component;
//    }
}
