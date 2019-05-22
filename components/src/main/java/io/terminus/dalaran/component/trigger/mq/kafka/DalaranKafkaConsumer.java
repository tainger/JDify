package io.terminus.dalaran.component.trigger.mq.kafka;

import io.terminus.dalaran.BodyType;
import io.terminus.dalaran.DalaranTrigger;
import io.terminus.dalaran.annotation.Trigger;
import org.apache.camel.model.RouteDefinition;

/**
 * Created by jingdi on 2019/5/20
 */
@Trigger(value = "kafka-consumer", configType = DalaranKafkaConsumerConfig.class, allowBodyTypes = {BodyType.JSON, BodyType.XML}, serializedBody = true)
public class DalaranKafkaConsumer implements DalaranTrigger<DalaranKafkaConsumerConfig> {

    @Override
    public void buildFromRoute(RouteDefinition route, DalaranKafkaConsumerConfig config) {
        String uri = "kafka:" + config.getTopic()
                + "?brokers=" + config.getBrokers()
                + "&groupId=" + config.getGroupId();
        if (!Boolean.valueOf(config.getAutocommit())) {
            uri = uri + "&autoCommitEnable=false"
                    + "&allowManualCommit=true"
                    + "&breakOnFirstError=true";
        }
        route.from(uri).to("log:consumer");
    }
}
