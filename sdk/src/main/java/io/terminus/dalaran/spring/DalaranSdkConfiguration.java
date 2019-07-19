package io.terminus.dalaran.spring;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(DalaranSdkConfiguration.class)
@Import(DalaranBeanDefinitionRegistrar.class)
public class DalaranSdkConfiguration {
}
