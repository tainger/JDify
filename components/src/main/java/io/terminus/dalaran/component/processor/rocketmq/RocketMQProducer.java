package io.terminus.dalaran.component.processor.rocketmq;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.core.component.DalaranMessageBodyCustomConverter;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranModelTypeContext;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by jingdi on 2019/6/19
 */
@Processor(
        value = "rocketmq-producer",
        name = "RocketMQ 消息发送器",
        order = 13,
        bodyType = "JSON",
        configType = RocketMQProducerConfig.class
)
public class RocketMQProducer implements DalaranProcessor<RocketMQProducerConfig>, DalaranMessageBodyCustomConverter<RocketMQProducerConfig> {

    @Autowired
    private DalaranModelTypeContext converterContext;

    private static final String CAMEL_ROCKET_MQ_URI = "rocketmq:?nameServer=%s&groupId=%s&topic=%s&tags=%s&useAliCloudOns=%s&accessKey=%s&secretKey=%s" +
            "&messageSharding=%s";

    @Override
    public void configure(ProcessorDefinition route, RocketMQProducerConfig config) {
        RocketMQConnector connector = config.getConnector();
        String uri = String.format(CAMEL_ROCKET_MQ_URI, connector.getNameServer(), config.getProducerGroup(),
                config.getTopic(), config.getTags(), connector.getUseAliCloudOns(),
                connector.getAccessKey(), connector.getSecretKey(), config.isMessageSharding());
        route.to(uri);
    }

    @Override
    public boolean customBodyConvert(RouteDefinition route, RocketMQProducerConfig config, String currentBodyType) {
        if (!config.isMessageSharding()) {
            return true;
        }
        if (!DalaranConstants.OBJECT_MODEL_TYPE.equalsIgnoreCase(currentBodyType) && !DalaranConstants.UNKNOWN_MODEL_TYPE.equalsIgnoreCase(currentBodyType)) {
            converterContext.toObject(route, config.getInModel(), currentBodyType);
        }
//        route.split(body());

        return false;
    }
}
