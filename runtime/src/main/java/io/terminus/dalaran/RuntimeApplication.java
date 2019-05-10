package io.terminus.dalaran;

import io.terminus.dalaran.configura.DalaranAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"io.terminus.dalaran"})
@EntityScan(basePackages = {"io.terminus.dalaran"})
@EnableJpaRepositories(basePackages = {"io.terminus.dalaran"})
@EnableScheduling
@Import(DalaranAutoConfiguration.class)
public class RuntimeApplication {

    @Bean
    public DalaranTraceLogger dalaranTraceLogger() {
        return new DefaultJpaDalaranTraceLogger();
    }

    @Bean
    public AbstractDalaranLoader dalaranLoader() {
        return new ReleasedFlowLoader();
    }

    public static void main(String[] args) {
        SpringApplication.run(RuntimeApplication.class);
    }
}
