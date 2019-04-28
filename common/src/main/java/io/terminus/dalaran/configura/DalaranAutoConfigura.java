package io.terminus.dalaran.configura;

import io.terminus.dalaran.*;
import io.terminus.dalaran.support.component.DefaultDalaranComponentContext;
import io.terminus.dalaran.support.convert.DefaultDalaranConverterContext;
import io.terminus.dalaran.support.flow.DefaultDalaranCamelContext;
import io.terminus.dalaran.support.flow.FlowBuilder;
import io.terminus.dalaran.support.trace.TracingErrorHandlerFactory;
import io.terminus.dalaran.trace.DefaultJpaDalaranTraceLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DalaranAutoConfigura {
    @Bean
    public DalaranContext dalaranContext(
            DalaranConverterContext converterContext,
            DalaranComponentContext componentContext,
            FlowBuilder flowBuilder
    ) {
        return new DefaultDalaranCamelContext(flowBuilder, converterContext, componentContext);
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
    public TracingErrorHandlerFactory tracingErrorHandlerFactory(DalaranTraceLogger traceLogger) {
        return new TracingErrorHandlerFactory(traceLogger);
    }

    @Bean
    public DalaranTraceLogger dalaranTraceLogger() {
        return new DefaultJpaDalaranTraceLogger();
    }

    @Bean
    public DalaranLoader dalaranLoader() {
        return new DalaranLoader(false);
    }

    @Bean
    public FlowBuilder flowBuilder(DalaranConverterContext converterContext,
                                   DalaranComponentContext componentContext,
                                   DalaranTraceLogger traceLogger,
                                   TracingErrorHandlerFactory errorHandlerFactory) {
        return new FlowBuilder(converterContext, componentContext, traceLogger, errorHandlerFactory);
    }
}
