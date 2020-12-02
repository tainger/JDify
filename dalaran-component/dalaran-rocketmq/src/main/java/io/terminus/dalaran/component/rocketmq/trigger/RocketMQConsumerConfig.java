package io.terminus.dalaran.component.rocketmq.trigger;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.RocketMQConnector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.AllModelConfig;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import lombok.Data;

/**
 * Created by jingdi on 2019/6/19
 */
@Data
public class RocketMQConsumerConfig extends AllModelConfig implements ConnectorConfig<RocketMQConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private RocketMQConnector connector;

    @ConfigFieldInfo(label = "RocketMQ 连接器", inputType = FieldInputType.Connector, connectorType = RocketMQConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "主题", inputType = FieldInputType.String)
    private String topic;

    @ConfigFieldInfo(label = "消费者组", inputType = FieldInputType.String)
    private String consumerGroup;

    @ConfigFieldInfo(label = "tag", inputType = FieldInputType.String, required = false)
    private String tags;

    @ConfigFieldInfo(label = "消费自动确认", inputType = FieldInputType.Switch, defaultValue = "true")
    private boolean autocommit = true;
}
