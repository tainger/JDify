package io.terminus.dalaran.component.processor.context;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.component.common.expression.ExpressionParser;
import io.terminus.dalaran.component.utils.ContextUtils;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Processor(
        value = "set-context",
        order = 20,
        configType = SetContextConfig.class,
        description = "设置全局上下文：设置流程中会使用到的全局变量"
)
public class SetContextProcessor implements DalaranProcessor<SetContextConfig> {
    @Override
    public void configure(ProcessorDefinition route, SetContextConfig config) {
        route.process(exchange -> {
            Map<String, Object> context = ContextUtils.setExchange(exchange);
            // TODO value 可以是一个表达式, 先不搞吧, 表达式需要整体处理
            String value = config.getValue();
            if (StringUtils.startsWith(value, DalaranConstants.DALARAN_EXPRESSION_HEADER)) {
                ExpressionParser parser = new ExpressionParser();
                context.put(config.getKey(), parser.parse(value, exchange));
            } else {
                context.put(config.getKey(), value);
            }
        });
    }
}
