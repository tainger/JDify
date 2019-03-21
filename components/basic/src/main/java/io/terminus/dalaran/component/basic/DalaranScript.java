package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.BodyMode;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.DalaranComponent;
import org.apache.camel.model.ProcessorDefinition;

@DalaranComponent(value = "script", configType = DalaranScriptConfig.class, bodyMode = BodyMode.Object)
public class DalaranScript implements DalaranProcessor<DalaranScriptConfig> {

    @Override
    public void configure(ProcessorDefinition route, DalaranScriptConfig config) {
        switch (config.getType()) {
            case JavaScript: {
                String content = config.getScript() + "\nexchange.out.body = execute(request.header, request.body)";
                ((ProcessorDefinition) route.script().javaScript(content)).end();
            }
        }
    }
}
