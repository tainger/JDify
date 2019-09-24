package io.terminus.dalaran.component.processor.rocketmq;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import lombok.Data;

/**
 * Created by jingdi on 2019/6/19
 */
@Data
public class RocketMQProducerConfig extends OutModelConfig implements ConnectorConfig<RocketMQConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private RocketMQConnector connector;

    @ConfigFieldInfo(label = "RocketMQ 连接器", inputType = FieldInputType.Connector, connectorType = RocketMQConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "主题", inputType = FieldInputType.String)
    private String topic;

    @ConfigFieldInfo(label = "生产者组", inputType = FieldInputType.String)
    private String producerGroup;
}
