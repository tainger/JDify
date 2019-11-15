package io.terminus.dalaran.component.processor.kafka;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.processor.mq.DalaranMQProducerConfig;
import io.terminus.dalaran.component.connector.KafkaConnector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/16
 */
@Data
public class DalaranKafkaProducerConfig extends DalaranMQProducerConfig implements ConnectorConfig<KafkaConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private KafkaConnector connector;

    @ConfigFieldInfo(label = "kafka 连接器", inputType = FieldInputType.Connector, connectorType = KafkaConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "超时时间", inputType = FieldInputType.Integer)
    private Long timeout;
}
