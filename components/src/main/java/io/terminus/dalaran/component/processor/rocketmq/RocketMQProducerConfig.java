package io.terminus.dalaran.component.processor.rocketmq;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.processor.mq.DalaranMQProducerConfig;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import lombok.Data;

/**
 * Created by jingdi on 2019/6/19
 */
@Data
public class RocketMQProducerConfig extends DalaranMQProducerConfig implements ConnectorConfig<RocketMQConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private RocketMQConnector connector;

    @ConfigFieldInfo(label = "RocketMQ 连接器", inputType = FieldInputType.Connector, connectorType = RocketMQConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "生产者组", inputType = FieldInputType.String)
    private String producerGroup;

    @ConfigFieldInfo(label = "tag", inputType = FieldInputType.String, required = false)
    private String tags;
}
