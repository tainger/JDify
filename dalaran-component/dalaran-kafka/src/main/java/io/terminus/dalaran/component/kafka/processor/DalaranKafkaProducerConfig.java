package io.terminus.dalaran.component.kafka.processor;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.SourceType;
import io.terminus.dalaran.component.connector.KafkaConnector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/16
 */
@Data
public class DalaranKafkaProducerConfig extends OutModelConfig implements ConnectorConfig<KafkaConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private KafkaConnector connector;

    @ConfigFieldInfo(label = "kafka 连接器", inputType = FieldInputType.Connector,
            connectorType = KafkaConnector.class, sourceType = SourceType.CONNECTOR)
    private String connectorId;

    @ConfigFieldInfo(label = "主题", inputType = FieldInputType.String)
    private String topic;

    @ConfigFieldInfo(label = "消息拆分", inputType = FieldInputType.Switch, required = false, defaultValue = "false")
    private boolean messageSharding;

    @ConfigFieldInfo(label = "超时时间", inputType = FieldInputType.Integer)
    private Long timeout;
}
