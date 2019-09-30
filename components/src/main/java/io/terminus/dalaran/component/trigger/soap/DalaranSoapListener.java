package io.terminus.dalaran.component.trigger.soap;

import io.terminus.dalaran.component.trigger.rest.processor.QueryStringSignProcessor;
import io.terminus.dalaran.component.trigger.soap.model.SoapAuthType;
import io.terminus.dalaran.component.trigger.soap.processor.SoapBasicSignProcessor;
import io.terminus.dalaran.component.trigger.soap.processor.SoapTriggerProcessor;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.core.context.DalaranClientContext;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by jingdi on 2019/6/13
 */
@Trigger(
        value = "soap-listener",
        name = "Soap 监听器",
        order = 13,
        configType = SoapListenerConfig.class,
        bodyType = "SOAP"
)
public class DalaranSoapListener implements DalaranTrigger<SoapListenerConfig> {

    @Autowired
    private DalaranClientContext clientContext;

    @Override
    public void buildFromRoute(RouteDefinition route, SoapListenerConfig config) {
        String uri = "netty4-http:" + config.getProtocol().name().toLowerCase() +
                "://0.0.0.0:" + config.getPort() + config.getPath() +
                "?httpMethodRestrict=" + config.getMethod();
        route.from(uri).process(new SoapTriggerProcessor());

        if (!config.isEnableSign()) {
            return;
        }
        if (config.getAuthType() == SoapAuthType.BASIC) {
            route.process(new SoapBasicSignProcessor(clientContext.getAllClient()));
        }
        if (config.getAuthType() == SoapAuthType.CUSTOM) {
            route.process(new QueryStringSignProcessor(clientContext.getAllClient()));
        }
    }
}
