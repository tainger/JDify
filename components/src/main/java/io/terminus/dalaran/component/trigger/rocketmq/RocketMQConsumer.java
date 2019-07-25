package io.terminus.dalaran.component.trigger.rocketmq;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.model.BodyType;
import org.apache.camel.model.RouteDefinition;

/**
 * Created by jingdi on 2019/6/19
 */
@Trigger(
        value = "rocketmq-consumer",
        name = "RocketMQ 消费者",
        order = 15,
        configType = RocketMQConsumerConfig.class,
        allowBodyTypes = {BodyType.JSON, BodyType.XML},
        inputSerializeType = BodySerializeType.Serialized,
        outputSerializeType = BodySerializeType.Serialized
)
public class RocketMQConsumer implements DalaranTrigger<RocketMQConsumerConfig> {

    @Override
    public void buildFromRoute(RouteDefinition route, RocketMQConsumerConfig config) {
        String uri = "rocketmq:?nameServer=" + config.getConnector().getNameServer()
                + "&groupId=" + config.getConsumerGroup()
                + "&topic=" + config.getTopic();
        route.from(uri).to("log:consumer");
    }
}
