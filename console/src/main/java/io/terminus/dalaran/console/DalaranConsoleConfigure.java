package io.terminus.dalaran.console;

import io.terminus.dalaran.console.log.*;
import io.terminus.dalaran.core.log.DalaranTraceLogger;
import io.terminus.dalaran.core.resource.repository.TracingLogRepository;
import io.terminus.dalaran.core.spring.DalaranAutoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Locale;

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
    public LocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setCookieName("locale");
        localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return localeResolver;
    }

    @Bean
    @ConditionalOnProperty(value = "terminus.dalaran.log.policy", havingValue = "archive")
    public ArchiveLogRetentionPolicy logRetentionPolicy() {
        return new ArchiveLogRetentionPolicy();
    }

    @Bean
    @ConditionalOnBean(ArchiveLogRetentionPolicy.class)
    public ArchiveLogRetentionPolicyExecutor archiveLogRetentionPolicyExecutor(ArchiveLogRetentionPolicy logRetentionPolicy) {
        return new ArchiveLogRetentionPolicyExecutor(logRetentionPolicy);
    }


    @Bean
    @ConditionalOnBean(ArchiveLogRetentionPolicy.class)
    public RemoveArchivingLogPolicy removeArchivingLogPolicy(@Value("${terminus.dalaran.log.archive.duration}") Integer duration,
                                                             @Value("${terminus.dalaran.log.archive.dbName}") String dbName
    ) {
        return new RemoveArchivingLogPolicy(duration, dbName);
    }


    @Bean
    @ConditionalOnBean(RemoveArchivingLogPolicy.class)
    public RemoveArchivingLogPolicyExecutor removeArchivingLogPolicyExecutor(RemoveArchivingLogPolicy removeArchivingLogPolicy) {
        return new RemoveArchivingLogPolicyExecutor(removeArchivingLogPolicy);
    }

    @Bean
    @ConditionalOnProperty(value = "terminus.dalaran.log.policy", havingValue = "delete")
    public DeleteLogRetentionPolicy deleteLogRetentionPolicy(@Value("${terminus.dalaran.log.duration}") Integer duration) {
        return new DeleteLogRetentionPolicy(duration);
    }


    @Bean
    @ConditionalOnBean(DeleteLogRetentionPolicy.class)
    public DeleteLogRetentionPolicyExecutor deleteLogRetentionPolicyExecutor(DeleteLogRetentionPolicy deleteLogRetentionPolicy) {
        return new DeleteLogRetentionPolicyExecutor(deleteLogRetentionPolicy);
    }



}
