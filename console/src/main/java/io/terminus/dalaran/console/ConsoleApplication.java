package io.terminus.dalaran.console;

import io.terminus.dalaran.DalaranTraceLogger;
import io.terminus.dalaran.configura.DalaranAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = {"io.terminus.dalaran"})
@EnableSwagger2
@EntityScan(basePackages = {"io.terminus.dalaran"})
@EnableJpaRepositories(basePackages = {"io.terminus.dalaran"})
@Import(DalaranAutoConfiguration.class)
public class ConsoleApplication {

    @Bean
    public TestFlowLoader testFlowLoader() {
        return new TestFlowLoader();
    }

    @Bean
    public DalaranTraceLogger dalaranTraceLogger() {
        return new TestTraceLogger();
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsoleApplication.class);
    }
}
