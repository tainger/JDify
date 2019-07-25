package io.terminus.dalaran.spring;

import io.terminus.dalaran.DalaranIntegration;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class DalaranComponentProvider extends ClassPathScanningCandidateComponentProvider {

    public DalaranComponentProvider(ResourceLoader resourceLoader, Environment environment) {
        super(false);
        super.setResourceLoader(resourceLoader);
        super.setEnvironment(environment);
        super.addIncludeFilter(new AnnotationTypeFilter(DalaranIntegration.class));

    }

    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        boolean isTopLevelType = !beanDefinition.getMetadata().hasEnclosingClass();
        return beanDefinition.getMetadata().isInterface() && isTopLevelType;
    }
}
