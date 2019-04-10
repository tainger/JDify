package io.terminus.dalaran.console;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EntityScan(basePackages = {"io.terminus.dalaran"} )
@EnableJpaRepositories(basePackages = {"io.terminus.dalaran"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
