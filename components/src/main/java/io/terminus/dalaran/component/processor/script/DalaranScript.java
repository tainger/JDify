package io.terminus.dalaran.component.processor.script;

import io.terminus.dalaran.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.camel.model.ProcessorDefinition;

import static io.terminus.dalaran.DalaranConstants.DALARAN_CONTEXT_EXCHANGE;

@Processor(
        value = "script",
        name = "执行脚本",
        order = 12,
        configType = DalaranScriptConfig.class,
        inputSerializeType = BodySerializeType.Object,
        outputSerializeType = BodySerializeType.Object
)
public class DalaranScript implements DalaranProcessor<DalaranScriptConfig> {

    @Override
    public void configure(ProcessorDefinition route, DalaranScriptConfig config) {
        switch (config.getType()) {
            case JavaScript: {
                String content = "function execute(body, headers, context) {\n";
                content += config.getScript();
                content += "\n}\n";
                content += "exchange.out.body = execute(request.body, request.headers, exchange.properties." + DALARAN_CONTEXT_EXCHANGE + ")";
                route.script().javaScript(content);
                // TODO 这里很奇怪, 处理一下 array 的输出, 因为 java script 产出的数组其实也是一个 map....
                route.process(exchange -> {
                    Object body = exchange.getIn().getBody();
                    if (body instanceof ScriptObjectMirror && ((ScriptObjectMirror) body).isArray()) {
                        exchange.getOut().setBody(((ScriptObjectMirror) body).values());
                    } else {
                        exchange.getOut().setBody(body);
                    }
                });
                route.end();
                break;
            }
            case Groovy: {
                String content = "def execute(Object body, Map headers, Map context) {\n";
                content += config.getScript();
                content += "\n}\n";
                content += "exchange.out.body = execute(request.body, request.headers, exchange.properties." + DALARAN_CONTEXT_EXCHANGE + ")";
                route.script().groovy(content);
                route.end();
                break;
            }
        }
    }
}
