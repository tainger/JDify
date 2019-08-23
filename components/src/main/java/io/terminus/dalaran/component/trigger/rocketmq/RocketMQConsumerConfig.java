package io.terminus.dalaran.component.trigger.rocketmq;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.component.processor.rocketmq.RocketMQConnector;
import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

/**
 * Created by jingdi on 2019/6/19
 */
@Data
public class RocketMQConsumerConfig {

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
}
