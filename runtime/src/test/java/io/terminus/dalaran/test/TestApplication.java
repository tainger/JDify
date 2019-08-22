package io.terminus.dalaran.test;

import io.terminus.dalaran.core.log.DalaranTraceLogger;
import io.terminus.dalaran.core.resource.DalaranResourceLoader;
import io.terminus.dalaran.core.spring.DalaranAutoConfiguration;
import io.terminus.dalaran.runtime.DefaultJpaDalaranTraceLogger;
import io.terminus.dalaran.runtime.ReleasedFlowInitializer;
import io.terminus.dalaran.runtime.ReleasedResourceLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"io.terminus.dalaran"})
@Import(DalaranAutoConfiguration.class)
@EntityScan(basePackages = {"io.terminus.dalaran"})
@EnableJpaRepositories(basePackages = {"io.terminus.dalaran"})
@Slf4j
public class TestApplication {

    @Bean
    public DalaranTraceLogger dalaranTraceLogger() {
        return new DefaultJpaDalaranTraceLogger();
    }

    @Bean
    public DalaranResourceLoader dalaranResourceLoader() {
        return new ReleasedResourceLoader();
    }

    @Bean
    public ReleasedFlowInitializer releasedFlowInitializer() {
        return new ReleasedFlowInitializer();
    }
}
