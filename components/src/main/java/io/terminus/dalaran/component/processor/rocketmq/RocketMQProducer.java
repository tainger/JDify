package io.terminus.dalaran.component.processor.rocketmq;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Created by jingdi on 2019/6/19
 */
@Processor(
        value = "rocketmq-producer",
        name = "RocketMQ 消息发送器",
        order = 13,
        configType = RocketMQProducerConfig.class
)
public class RocketMQProducer implements DalaranProcessor<RocketMQProducerConfig> {

    private static final String CAMEL_ROCKET_MQ_URI = "rocketmq:?nameServer=%s&groupId=%s&topic=%s&tags=%s&useAliCloudOns=%s&accessKey=%s&secretKey=%s&messageSharding=%s";

    @Override
    public void configure(ProcessorDefinition route, RocketMQProducerConfig config) {
        RocketMQConnector connector = config.getConnector();
        String uri = String.format(CAMEL_ROCKET_MQ_URI, connector.getNameServer(), config.getProducerGroup(),
                config.getTopic(), config.getTags(), connector.getUseAliCloudOns(),
                connector.getAccessKey(), connector.getSecretKey());
        route.to(uri);
    }
}
