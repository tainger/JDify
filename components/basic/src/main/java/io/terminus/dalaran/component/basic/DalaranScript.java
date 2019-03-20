package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.DalaranComponent;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.Map;

@DalaranComponent(value = "script", configType = DalaranScriptConfig.class)
public class DalaranScript implements DalaranProcessor<DalaranScriptConfig> {

    @Override
    public void configure(ProcessorDefinition route, DalaranScriptConfig config) {

        route.unmarshal().json(JsonLibrary.Gson, Map.class);
        switch (config.getType()) {
            case JavaScript: {
                String content = config.getScript() + "\nexchange.out.body = execute(request.header, request.body)";
                ((ProcessorDefinition) route.script().javaScript(content)).end();
            }
        }
        route.marshal().json(JsonLibrary.Gson);
    }
}
