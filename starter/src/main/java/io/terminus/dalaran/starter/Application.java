package io.terminus.dalaran.starter;

import io.terminus.dalaran.DalaranLoader;
import io.terminus.dalaran.configura.DalaranAutoConfigura;
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
@Import(DalaranAutoConfigura.class)
public class Application {

    @Bean
    public DalaranLoader dalaranLoader() {
        return new DalaranLoader(false);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
