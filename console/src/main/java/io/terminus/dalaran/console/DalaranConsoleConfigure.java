package io.terminus.dalaran.console;

import io.terminus.dalaran.console.log.DeleteLogRetentionPolicy;
import io.terminus.dalaran.console.log.LogRetentionPolicy;
import io.terminus.dalaran.console.log.LogRetentionPolicyExecutor;
import io.terminus.dalaran.core.log.DalaranTraceLogger;
import io.terminus.dalaran.core.resource.repository.TracingLogRepository;
import io.terminus.dalaran.core.spring.DalaranAutoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@ComponentScan({"io.terminus.dalaran"})
@EntityScan(basePackages = {"io.terminus.dalaran.console.entity", "io.terminus.dalaran.core.resource.entity"})
@EnableScheduling
@EnableJpaRepositories(basePackages = {"io.terminus.dalaran.console.repository", "io.terminus.dalaran.core.resource.repository"})
@Import(DalaranAutoConfiguration.class)
public class DalaranConsoleConfigure {

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

    @Bean
    @ConditionalOnMissingBean(LogRetentionPolicy.class)
    @ConditionalOnProperty(value = "terminus.dalaran.log.policy", havingValue = "delete")
    public LogRetentionPolicy logRetentionPolicy(@Value("${terminus.dalaran.log.duration: 14}") Integer duration) {
        return new DeleteLogRetentionPolicy(duration);
    }

    @Bean
    @ConditionalOnBean(LogRetentionPolicy.class)
    public LogRetentionPolicyExecutor logRetentionPolicyExecutor(LogRetentionPolicy logRetentionPolicy) {
        return new LogRetentionPolicyExecutor(logRetentionPolicy);
    }
}
