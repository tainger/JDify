package io.terminus.dalaran.open.core;

import io.terminus.dalaran.open.common.annotation.OpenService;
import io.terminus.dalaran.open.common.model.OpenServiceInfo;
import io.terminus.dalaran.open.common.service.DalaranOpenService;
import io.terminus.dalaran.open.common.service.DalaranOpenServiceContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class DalaranOpenServiceLoader implements BeanPostProcessor {

    private DalaranOpenServiceContext dalaranOpenServiceContext;

    public DalaranOpenServiceLoader(DalaranOpenServiceContext dalaranOpenServiceContext) {
        this.dalaranOpenServiceContext = dalaranOpenServiceContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof DalaranOpenService)) {
            return bean;
        }
        OpenService openService = bean.getClass().getAnnotation(OpenService.class);
        dalaranOpenServiceContext.registerService(openService.channel(), openService.service(), new OpenServiceInfo((DalaranOpenService) bean, openService.inModel(), openService.outModel()));
        return bean;
    }
}
