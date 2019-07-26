package io.terminus.dalaran.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(value = "trantor.dalaran.runtimeUrl")
@EnableConfigurationProperties(DalaranSdkConfiguration.class)
@Import(DalaranBeanDefinitionRegistrar.class)
@ComponentScan("io.terminus.dalaran")
public class DalaranSdkConfiguration {
}
