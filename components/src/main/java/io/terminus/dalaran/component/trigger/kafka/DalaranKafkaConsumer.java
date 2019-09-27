package io.terminus.dalaran.component.trigger.kafka;

import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import org.apache.camel.model.RouteDefinition;

/**
 * Created by jingdi on 2019/5/20
 */
@Trigger(
        value = "kafka-consumer",
        name = "Kafka 消费者",
        order = 14,
        configType = DalaranKafkaConsumerConfig.class
)
public class DalaranKafkaConsumer implements DalaranTrigger<DalaranKafkaConsumerConfig> {

    @Override
    public void buildFromRoute(RouteDefinition route, DalaranKafkaConsumerConfig config) {
        String uri = "kafka:" + config.getTopic()
                + "?brokers=" + config.getConnector().getBrokers()
                + "&groupId=" + config.getGroupId();
        if (!Boolean.valueOf(config.getAutocommit())) {
            uri = uri + "&autoCommitEnable=false"
                    + "&allowManualCommit=true"
                    + "&breakOnFirstError=true";
        }
        route.from(uri);
    }
}
