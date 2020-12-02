package io.terminus.dalaran.component.rocketmq.processor;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.RocketMQConnector;
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

    @ConfigFieldInfo(label = "消息拆分", inputType = FieldInputType.Switch, required = false, defaultValue = "false")
    private boolean messageSharding;

    @ConfigFieldInfo(label = "生产者组", inputType = FieldInputType.String)
    private String producerGroup;

    @ConfigFieldInfo(label = "tag", inputType = FieldInputType.String, required = false)
    private String tags;

    @ConfigFieldInfo(label = "异步发送", inputType = FieldInputType.Switch, required = false)
    private Boolean async = false;
}
