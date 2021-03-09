package io.terminus.dalaran.component.kafka.trigger;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.KafkaConnector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.AllModelConfig;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/20
 */
@Data
public class DalaranKafkaConsumerConfig extends AllModelConfig implements ConnectorConfig<KafkaConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private KafkaConnector connector;

    @ConfigFieldInfo(label = "kafka 连接器", inputType = FieldInputType.Connector, connectorType = KafkaConnector.class)
    private String connectorId;

    @ConfigFieldInfo(label = "主题", inputType = FieldInputType.String)
    private String topic;

    @ConfigFieldInfo(label = "消费组id", inputType = FieldInputType.String)
    private String groupId;

    @ConfigFieldInfo(label = "自动提交", inputType = FieldInputType.Switch, defaultValue = "false")
    private Boolean autocommit = false;

}
