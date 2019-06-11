package io.terminus.dalaran.core.spring;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class DalaranComponentLoader implements BeanPostProcessor {

    private DalaranComponentContext componentContext;

    public DalaranComponentLoader(DalaranComponentContext componentContext) {
        this.componentContext = componentContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
        if (bean instanceof DalaranProcessor) {
            componentContext.addProcessor((DalaranProcessor) bean);
        }
        if (bean instanceof DalaranTrigger) {
            componentContext.addTrigger((DalaranTrigger) bean);
        }
        return bean;
    }
}
