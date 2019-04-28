package io.terminus.dalaran.component.processor.dubbo;

import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

@Processor(value = "dubbo-consumer", configType = DalaranDubboConsumerConfig.class)
public class DalaranDubboConsumer implements DalaranProcessor<DalaranDubboConsumerConfig> {

    private static final String DUBBO_PROVIDER_URI = "dubbo:?registryAddress=%s&serviceId=%s&method=%s&version=%s";

    @Override
    public void configure(ProcessorDefinition route, DalaranDubboConsumerConfig config) {
        String uri = String.format(DUBBO_PROVIDER_URI, config.getRegistryAddress(),
                config.getServiceId(), config.getMethod(), config.getVersion());
        route.to(uri);
    }
}
