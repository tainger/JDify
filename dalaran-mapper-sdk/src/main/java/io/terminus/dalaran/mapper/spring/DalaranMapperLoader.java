package io.terminus.dalaran.mapper.spring;

import io.terminus.dalaran.mapper.context.DalaranFunctionContext;
import io.terminus.dalaran.model.annotation.MappingFunction;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

public class DalaranMapperLoader implements BeanPostProcessor {

    private DalaranFunctionContext functionContext;

    public DalaranMapperLoader(DalaranFunctionContext functionContext) {
        this.functionContext = functionContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
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
        }
        return bean;
    }
}
