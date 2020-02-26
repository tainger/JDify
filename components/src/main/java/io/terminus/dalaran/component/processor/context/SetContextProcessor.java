package io.terminus.dalaran.component.processor.context;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.component.common.expression.ExpressionParser;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static io.terminus.dalaran.DalaranConstants.DALARAN_CONTEXT_EXCHANGE;

@Processor(
        value = "set-context",
        order = 20,
        configType = SetContextConfig.class
)
public class SetContextProcessor implements DalaranProcessor<SetContextConfig> {
    @Override
    public void configure(ProcessorDefinition route, SetContextConfig config) {
        route.process(exchange -> {
            Map<String, Object> context = exchange.getProperty(DALARAN_CONTEXT_EXCHANGE, Map.class);
            Map<String, Object> header = exchange.getIn().getHeader(exchange.getExchangeId(), Map.class);

            if (header == null) {
                header = new HashMap<>();
                exchange.getOut().setHeader(exchange.getExchangeId(), header);
            }

            if (context == null) {
                context = new HashMap<>();
                exchange.setProperty(DALARAN_CONTEXT_EXCHANGE, context);
            }
            // TODO value 可以是一个表达式, 先不搞吧, 表达式需要整体处理
            String value = config.getValue();
            if (StringUtils.startsWith(value, DalaranConstants.DALARAN_EXPRESSION_HEADER)) {
                ExpressionParser parser = new ExpressionParser();
                header.put(config.getKey(), parser.parse(value, exchange));
            } else {
                header.put(config.getKey(), value);
            }
        });
    }
}
