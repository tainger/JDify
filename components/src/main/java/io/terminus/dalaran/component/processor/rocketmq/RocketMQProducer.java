package io.terminus.dalaran.component.processor.rocketmq;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.model.BodyType;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Created by jingdi on 2019/6/19
 */
@Processor(value = "rocketmq-producer", configType = RocketMQProducerConfig.class,
        inputSerializeType = BodySerializeType.Object,
        outputSerializeType = BodySerializeType.Object,
        allowBodyTypes = {BodyType.JSON, BodyType.XML})
public class RocketMQProducer implements DalaranProcessor<RocketMQProducerConfig> {

    @Override
    public void configure(ProcessorDefinition route, RocketMQProducerConfig config) {
        String uri = "rocketmq:"
                + "?nameServer=" + config.getConnector().getNameServer()
                + "&groupId=" + config.getProducerGroup()
                + "&topic=" + config.getTopic();
        route.to(uri);
    }
}
