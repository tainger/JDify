package io.terminus.dalaran.console.autoconfigura;

import io.terminus.dalaran.DalaranComponentContext;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.DalaranConverterContext;
import io.terminus.dalaran.DalaranTraceLogger;
import io.terminus.dalaran.support.component.DefaultDalaranComponentContext;
import io.terminus.dalaran.support.convert.DefaultDalaranConverterContext;
import io.terminus.dalaran.support.flow.DefaultDalaranCamelContext;
import io.terminus.dalaran.support.trace.DalaranTracingLogRepository;
import io.terminus.dalaran.support.trace.DefaultJpaDalaranTraceLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DalaranAutoConfiguration {

    @Bean
    public DalaranContext dalaranContext(DalaranConverterContext converterContext, DalaranComponentContext componentContext, DalaranTraceLogger traceLogger) {
        return new DefaultDalaranCamelContext(converterContext, componentContext, traceLogger);
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
    public DalaranTraceLogger dalaranTraceLogger() {
        return new DefaultJpaDalaranTraceLogger();
    }
}
