package io.terminus.dalaran.component.processor.retry;

import io.terminus.dalaran.BodySerializeType;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

// TODO 不对外暴露
@Processor(value = "retry-fragment", serializeType = BodySerializeType.All, configType = RetryConfig.class)
public class RetryFragment implements DalaranProcessor<RetryConfig> {
    @Override
    public void configure(ProcessorDefinition route, RetryConfig config) {
        route.onException(Throwable.class).maximumRedeliveries(config.getMaxRetry()).redeliveryDelay(config.getRetryDelay());
    }
}
