package io.terminus.dalaran.component.processor.kafka;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ComponentBasicConfig;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/17
 */
@Data
public class KafkaConnector extends ComponentBasicConfig {

    @ConfigFieldInfo(label = "集群地址", inputType = FieldInputType.String)
    private String brokers;
}