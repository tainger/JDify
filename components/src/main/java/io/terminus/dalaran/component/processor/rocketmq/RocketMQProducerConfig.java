package io.terminus.dalaran.component.processor.rocketmq;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

/**
 * Created by jingdi on 2019/6/19
 */
@Data
public class RocketMQProducerConfig {

    @ConfigFieldInfo(label = "注册中心", inputType = FieldInputType.String)
    private String nameServer;

    @ConfigFieldInfo(label = "主题", inputType = FieldInputType.String)
    private String topic;

    @ConfigFieldInfo(label = "生产者组", inputType = FieldInputType.String)
    private String producerGroup;
}
