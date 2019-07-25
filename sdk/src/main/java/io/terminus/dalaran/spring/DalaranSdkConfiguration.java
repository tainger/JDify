package io.terminus.dalaran.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(value = "terminus.dalaran.enable", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DalaranSdkConfiguration.class)
@Import(DalaranBeanDefinitionRegistrar.class)
public class DalaranSdkConfiguration {
}
