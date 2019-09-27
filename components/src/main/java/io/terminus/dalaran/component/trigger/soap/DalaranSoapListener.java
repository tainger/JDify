package io.terminus.dalaran.component.trigger.soap;

import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import org.apache.camel.model.RouteDefinition;

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

    @Override
    public void buildFromRoute(RouteDefinition route, SoapListenerConfig config) {
        String uri = "netty4-http:" + config.getProtocol().name().toLowerCase() +
                "://0.0.0.0:" + config.getPort() + config.getPath() +
                "?httpMethodRestrict=" + config.getMethod();
        route.from(uri).process(new SoapTriggerProcessor());
    }
}
