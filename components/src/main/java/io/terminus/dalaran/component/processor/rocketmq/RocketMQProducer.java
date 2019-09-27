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

    @Override
    public void configure(ProcessorDefinition route, RocketMQProducerConfig config) {
        String uri = "rocketmq:"
                + "?nameServer=" + config.getConnector().getNameServer()
                + "&groupId=" + config.getProducerGroup()
                + "&topic=" + config.getTopic() + "&tags=" + config.getTags();
        route.to(uri);
    }
}
