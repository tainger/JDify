package io.terminus.dalaran.configura;

import io.terminus.dalaran.*;
import io.terminus.dalaran.support.component.DefaultDalaranComponentContext;
import io.terminus.dalaran.support.convert.DefaultDalaranConverterContext;
import io.terminus.dalaran.support.flow.DefaultDalaranCamelContext;
import io.terminus.dalaran.support.flow.FlowBuilder;
import io.terminus.dalaran.support.service.DefaultDalaranServiceContext;
import io.terminus.dalaran.support.trace.TracingErrorHandlerFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.Registry;
import org.apache.camel.spring.spi.ApplicationContextRegistry;
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
            FlowBuilder flowBuilder,
            CamelContext camelContext
    ) {
        return new DefaultDalaranCamelContext(camelContext, converterContext, componentContext, serviceContext, flowBuilder);
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
    public FlowBuilder flowBuilder(DalaranConverterContext converterContext,
                                   DalaranComponentContext componentContext,
                                   DalaranTraceLogger traceLogger,
                                   TracingErrorHandlerFactory errorHandlerFactory) {
        return new FlowBuilder(converterContext, componentContext, traceLogger, errorHandlerFactory);
    }
}
