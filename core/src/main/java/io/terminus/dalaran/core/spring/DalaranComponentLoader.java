package io.terminus.dalaran.core.spring;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.MappingFunction;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.context.DalaranFunctionContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

public class DalaranComponentLoader implements BeanPostProcessor {

    private DalaranComponentContext componentContext;
    private DalaranFunctionContext functionContext;

    public DalaranComponentLoader(DalaranComponentContext componentContext, DalaranFunctionContext functionContext) {
        this.componentContext = componentContext;
        this.functionContext = functionContext;
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
        MappingFunction mappingFunction = bean.getClass().getAnnotation(MappingFunction.class);
        if (mappingFunction != null) {
            Method method = null;
            for (Method declaredMethod : bean.getClass().getDeclaredMethods()) {
                if (mappingFunction.function().equals(declaredMethod.getName())) {
                    method = declaredMethod;
                }
            }
            if (method != null) {
                functionContext.addStaticFunction(mappingFunction.value(), mappingFunction.description(), bean, method);
            }
            // TODO else throw
        }
        return bean;
    }
}
