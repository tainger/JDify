package io.terminus.dalaran.runtime;

import io.terminus.dalaran.core.log.DalaranTraceLogger;
import io.terminus.dalaran.core.resource.DalaranResourceLoader;
import io.terminus.dalaran.core.spring.DalaranAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan({"io.terminus.dalaran"})
@EntityScan(basePackages = {"io.terminus.dalaran"})
@EnableJpaRepositories(basePackages = {"io.terminus.dalaran"})
@EnableScheduling
@Import(DalaranAutoConfiguration.class)
public class DalaranRuntimeConfigure {

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

    @Bean
    public DefaultAlarmManager alarmManagerInitializer() {
        return new DefaultAlarmManager();
    }

}
