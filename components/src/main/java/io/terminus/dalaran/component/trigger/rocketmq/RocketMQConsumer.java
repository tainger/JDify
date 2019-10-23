package io.terminus.dalaran.component.trigger.rocketmq;

import io.terminus.dalaran.component.processor.rocketmq.RocketMQConnector;
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

    private static final String CAMEL_ROCKET_MQ_URI = "rocketmq:?nameServer=%s&groupId=%s&topic=%s&tags=%s&useAliCloudOns=%s" +
            "&accessKey=%s&secretKey=%s&autocommit=%s";

    @Override
    public void buildFromRoute(RouteDefinition route, RocketMQConsumerConfig config) {
        RocketMQConnector connector = config.getConnector();
        String uri = String.format(CAMEL_ROCKET_MQ_URI, connector.getNameServer(), config.getConsumerGroup(),
                config.getTopic(), config.getTags(), connector.getUseAliCloudOns(),
                connector.getAccessKey(), connector.getSecretKey(), config.getAutocommit());
        route.from(uri);
    }
}
