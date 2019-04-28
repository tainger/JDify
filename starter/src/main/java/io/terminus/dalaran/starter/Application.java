package io.terminus.dalaran.starter;

import io.terminus.dalaran.configura.DalaranAutoConfigura;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"io.terminus.dalaran"})
@EntityScan(basePackages = {"io.terminus.dalaran"})
@EnableJpaRepositories(basePackages = {"io.terminus.dalaran"})
@Import(DalaranAutoConfigura.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
