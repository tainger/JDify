package io.terminus.dalaran.component.rocketmq.processor;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.component.common.expression.ExpressionParser;
import io.terminus.dalaran.component.connector.RocketMQConnector;
import io.terminus.dalaran.core.component.DalaranMessageBodyCustomConverter;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranModelTypeContext;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by jingdi on 2019/6/19
 */
@Processor(
        value = "rocketmq-producer",
        order = 13,
        bodyType = "JSON",
        configType = RocketMQProducerConfig.class
)
public class RocketMQProducer implements DalaranProcessor<RocketMQProducerConfig>, DalaranMessageBodyCustomConverter<RocketMQProducerConfig> {

    @Autowired
    private DalaranModelTypeContext converterContext;

    private final String JSON_PATH_HEADER = "$.";

    private static final String CAMEL_ROCKET_MQ_URI = "rocketmq:?nameServer=%s&groupId=%s&topic=%s&tags=%s&useAliCloudOns=%s&accessKey=%s&secretKey=%s" +
            "&messageSharding=%s&timeout=%s&async=%s";

    @Override
    public void configure(ProcessorDefinition route, RocketMQProducerConfig config) {
        RocketMQConnector connector = config.getConnector();
        ExpressionParser parser = new ExpressionParser();
        String tags = config.getTags();
        if (StringUtils.contains(tags, DalaranConstants.DALARAN_EXPRESSION_HEADER)) {
            tags = JSON_PATH_HEADER + parser.parseBodyPath(tags);
        }
        String topic = config.getTopic();
        if (StringUtils.contains(topic, DalaranConstants.DALARAN_EXPRESSION_HEADER)) {
            topic = JSON_PATH_HEADER + parser.parseBodyPath(topic);
        }
        String uri = String.format(CAMEL_ROCKET_MQ_URI, connector.getNameServer(), config.getProducerGroup(),
                topic, tags, connector.getUseAliCloudOns(),
                connector.getAccessKey(), connector.getSecretKey(), config.isMessageSharding(), connector.getTimeout(), config.getAsync());
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
        return false;
    }
}
