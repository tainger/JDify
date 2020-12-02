package io.terminus.dalaran.component.connector;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.Connector;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/17
 */
@Data
@Connector("Kafka")
public class KafkaConnector {

    @ConfigFieldInfo(label = "集群地址", inputType = FieldInputType.String)
    private String brokers;
}