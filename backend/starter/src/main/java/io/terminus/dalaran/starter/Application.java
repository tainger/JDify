package io.terminus.dalaran.starter;

import io.terminus.dalaran.DalaranComponentContext;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.DalaranConverterContext;
import io.terminus.dalaran.DalaranTraceLogger;
import io.terminus.dalaran.support.component.DefaultDalaranComponentContext;
import io.terminus.dalaran.support.convert.DefaultDalaranConverterContext;
import io.terminus.dalaran.support.flow.DefaultDalaranCamelContext;
import io.terminus.dalaran.support.trace.DefaultJpaDalaranTraceLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"io.terminus.dalaran"})
@EnableJpaRepositories(basePackages = {"io.terminus.dalaran"})
public class Application {

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

    @Bean
    public DalaranLoader dalaranLoader() {
        return new DalaranLoader();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
