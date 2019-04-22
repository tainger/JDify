package io.terminus.dalaran.starter;

import io.terminus.dalaran.*;
import io.terminus.dalaran.support.component.DefaultDalaranComponentContext;
import io.terminus.dalaran.support.convert.DefaultDalaranConverterContext;
import io.terminus.dalaran.support.flow.DefaultDalaranCamelContext;
import io.terminus.dalaran.support.trace.TracingErrorHandlerFactory;
import io.terminus.dalaran.trace.DefaultJpaDalaranTraceLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"io.terminus.dalaran"})
@EntityScan(basePackages = {"io.terminus.dalaran"})
@EnableJpaRepositories(basePackages = {"io.terminus.dalaran"})
public class Application {

    @Bean
    public DalaranContext dalaranContext(
            DalaranConverterContext converterContext,
            DalaranComponentContext componentContext,
            DalaranTraceLogger traceLogger,
            TracingErrorHandlerFactory tracingErrorHandlerFactory
    ) {
        return new DefaultDalaranCamelContext(converterContext, componentContext, traceLogger, tracingErrorHandlerFactory);
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

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
