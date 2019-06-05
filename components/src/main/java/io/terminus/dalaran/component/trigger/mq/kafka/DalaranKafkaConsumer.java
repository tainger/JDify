package io.terminus.dalaran.component.trigger.mq.kafka;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.core.model.BodyType;
import org.apache.camel.model.RouteDefinition;

/**
 * Created by jingdi on 2019/5/20
 */
@Trigger(value = "kafka-consumer", configType = DalaranKafkaConsumerConfig.class, allowBodyTypes = {BodyType.JSON, BodyType.XML},
        inputSerializeType = BodySerializeType.Serialized,
        outputSerializeType = BodySerializeType.Serialized
)
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
