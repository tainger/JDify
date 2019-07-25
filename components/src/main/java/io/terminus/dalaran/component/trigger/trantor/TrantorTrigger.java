package io.terminus.dalaran.component.trigger.trantor;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.model.BodyType;
import org.apache.camel.model.RouteDefinition;

@Trigger(
        value = "Trantor-Integration",
        name = "Trantor 集成点",
        order = 11,
        configType = TrantorTriggerConfig.class,
        allowBodyTypes = {BodyType.JSON},
        inputSerializeType = BodySerializeType.Serialized,
        outputSerializeType = BodySerializeType.Serialized
)
public class TrantorTrigger implements DalaranTrigger<TrantorTriggerConfig> {

    @Override
    public void buildFromRoute(RouteDefinition route, TrantorTriggerConfig config) {
        // TODO 默认端口是 8080
        String uri = "netty4-http:http://0.0.0.0:8080/__dalaran-trantor/" + config.getKey() + "/" + config.getMethod() + "?httpMethodRestrict=POST";
        route.from(uri).convertBodyTo(String.class);
    }
}
