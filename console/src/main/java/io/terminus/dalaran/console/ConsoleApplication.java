package io.terminus.dalaran.console;

import io.terminus.dalaran.core.log.DalaranTraceLogger;
import io.terminus.dalaran.core.resource.repository.TracingLogRepository;
import io.terminus.dalaran.core.spring.DalaranAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = {"io.terminus.dalaran"})
@EnableSwagger2
@EntityScan(basePackages = {"io.terminus.dalaran.console.entity", "io.terminus.dalaran.core.resource.entity"})
@EnableJpaRepositories(basePackages = {"io.terminus.dalaran.console.repository", "io.terminus.dalaran.core.resource.repository"})
@Import(DalaranAutoConfiguration.class)
public class ConsoleApplication {

    @Bean
    public TestResourceLoader testFlowLoader() {
        return new TestResourceLoader();
    }

    @Bean
    public DalaranTraceLogger dalaranTraceLogger(TracingLogRepository tracingLogRepository) {
        return new TestTraceLogger(tracingLogRepository);
    }

    @Bean
    public TestFlowInitializer testFlowInitializer() {
        return new TestFlowInitializer();
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsoleApplication.class);
    }
}
