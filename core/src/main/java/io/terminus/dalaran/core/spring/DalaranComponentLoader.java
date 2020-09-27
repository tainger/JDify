package io.terminus.dalaran.core.spring;

import io.terminus.dalaran.core.component.DalaranBasicComponent;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.MappingFunction;
import io.terminus.dalaran.core.component.annotation.ModelType;
import io.terminus.dalaran.core.component.model.DalaranModelType;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.context.DalaranFunctionContext;
import io.terminus.dalaran.core.context.DalaranModelTypeContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

public class DalaranComponentLoader implements BeanPostProcessor {

    private DalaranComponentContext componentContext;
    private DalaranFunctionContext functionContext;
    private DalaranModelTypeContext modelTypeContext;

    public DalaranComponentLoader(DalaranComponentContext componentContext, DalaranFunctionContext functionContext, DalaranModelTypeContext modelTypeContext) {
        this.componentContext = componentContext;
        this.functionContext = functionContext;
        this.modelTypeContext = modelTypeContext;
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
        if (bean instanceof DalaranBasicComponent) {
            componentContext.addBasicComponent((DalaranBasicComponent) bean);
        }
        ModelType modelType = bean.getClass().getAnnotation(ModelType.class);
        if (modelType != null && bean instanceof DalaranModelType) {
            modelTypeContext.addModelType((DalaranModelType) bean);
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
