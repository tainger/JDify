package io.terminus.dalaran.component.trigger.rocketmq;

import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import org.apache.camel.model.RouteDefinition;

/**
 * Created by jingdi on 2019/6/19
 */
@Trigger(
        value = "rocketmq-consumer",
        name = "RocketMQ 消费者",
        order = 15,
        configType = RocketMQConsumerConfig.class
)
public class RocketMQConsumer implements DalaranTrigger<RocketMQConsumerConfig> {

    @Override
    public void buildFromRoute(RouteDefinition route, RocketMQConsumerConfig config) {
        String uri = "rocketmq:?nameServer=" + config.getConnector().getNameServer()
                + "&groupId=" + config.getConsumerGroup()
                + "&topic=" + config.getTopic() + "&tags=" + config.getTags();
        route.from(uri).to("log:consumer");
    }
}
