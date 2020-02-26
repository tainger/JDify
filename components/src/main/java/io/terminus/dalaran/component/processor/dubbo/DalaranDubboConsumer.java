package io.terminus.dalaran.component.processor.dubbo;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

@Processor(
        value = "dubbo-consumer",
        order = 12,
        configType = DalaranDubboConsumerConfig.class
)
public class DalaranDubboConsumer implements DalaranProcessor<DalaranDubboConsumerConfig> {

    private static final String DUBBO_PROVIDER_URI = "dubbo:?application=%s&registryAddress=%s&serviceId=%s&method=%s&version=%s&timeout=%s&parameterType=%s&retries=%s";

    @Override
    public void configure(ProcessorDefinition route, DalaranDubboConsumerConfig config) {
        String uri = String.format(DUBBO_PROVIDER_URI, config.getConnector().getApplication(), config.getConnector().getAddress(),
                config.getServiceId(), config.getMethod(), config.getVersion(), config.getTimeout(), config.getParameterType(), config.getRetries());
        route.to(uri).process(exchange -> {
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
            exchange.getOut().setBody(exchange.getIn().getBody());
        });
    }
}
