package io.terminus.dalaran.component.trigger.mq.kafka;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.AllModelConfig;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/20
 */
@Data
public class DalaranKafkaConsumerConfig extends AllModelConfig {

    @ConfigFieldInfo(label = "集群地址", inputType = FieldInputType.String)
    private String brokers;

    @ConfigFieldInfo(label = "主题", inputType = FieldInputType.String)
    private String topic;

    @ConfigFieldInfo(label = "消费组id", inputType = FieldInputType.String)
    private String groupId;

    @ConfigFieldInfo(label = "自动提交", inputType = FieldInputType.Select)
    private String autocommit = "false";

}
