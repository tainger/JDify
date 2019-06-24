package io.terminus.dalaran.component.trigger.kafka;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.component.processor.kafka.KafkaConnector;
import io.terminus.dalaran.core.component.FieldInputType;
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
    private Long connectorId;

    @ConfigFieldInfo(label = "主题", inputType = FieldInputType.String)
    private String topic;

    @ConfigFieldInfo(label = "消费组id", inputType = FieldInputType.String)
    private String groupId;

    @ConfigFieldInfo(label = "自动提交", inputType = FieldInputType.Select)
    private String autocommit = "false";

}
