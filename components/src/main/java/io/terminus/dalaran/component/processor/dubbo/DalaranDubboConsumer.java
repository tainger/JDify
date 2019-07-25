package io.terminus.dalaran.component.processor.dubbo;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

@Processor(
        value = "dubbo-consumer",
        name = "Dubbo 服务消费者",
        order = 12,
        configType = DalaranDubboConsumerConfig.class,
        inputSerializeType = BodySerializeType.Object,
        outputSerializeType = BodySerializeType.Object
)
public class DalaranDubboConsumer implements DalaranProcessor<DalaranDubboConsumerConfig> {

    private static final String DUBBO_PROVIDER_URI = "dubbo:?application=%s&registryAddress=%s&serviceId=%s&method=%s&version=%s&parameterType=%s";

    @Override
    public void configure(ProcessorDefinition route, DalaranDubboConsumerConfig config) {
        String uri = String.format(DUBBO_PROVIDER_URI, config.getConnector().getApplication(), config.getConnector().getAddress(),
                config.getServiceId(), config.getMethod(), config.getVersion(), config.getParameterType());
        route.to(uri);
    }
}
