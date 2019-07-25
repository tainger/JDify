package io.terminus.dalaran.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

public class DalaranBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    private ResourceLoader resourceLoader;
    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        DalaranComponentProvider scanner = new DalaranComponentProvider(resourceLoader, environment);
        String[] basePackages = environment.getRequiredProperty("trantor.module.scan-packages", String[].class);
        String runtimeUrl = environment.getRequiredProperty("trantor.dalaran.runtimeUrl");

        for (String basePackage : basePackages) {
            Set<BeanDefinition> components = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition component : components) {
                BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(DalaranFactoryBean.class);
                definition.addPropertyValue("type", component.getBeanClassName());
                definition.addPropertyValue("runtimeUrl", runtimeUrl);
                definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
                AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
                BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, importingClassMetadata.getClassName(), null);
                BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
            }
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
