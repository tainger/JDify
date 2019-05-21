package io.terminus.dalaran.component.processor.mq.kafka;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import io.terminus.dalaran.config.OutModelConfig;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/16
 */
@Data
public class DalaranKafkaProducerConfig extends OutModelConfig {

    @ConfigFieldInfo(label = "集群地址", inputType = FieldInputType.String)
    private String brokers;

    @ConfigFieldInfo(label = "主题", inputType = FieldInputType.String)
    private String topic;

    @ConfigFieldInfo(label = "超时时间", inputType = FieldInputType.Integer)
    private Long timeout;
}
