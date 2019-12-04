package io.terminus.dalaran.component.processor.mq;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import lombok.Data;

@Data
public class DalaranMQProducerConfig extends OutModelConfig {

    @ConfigFieldInfo(label = "主题", inputType = FieldInputType.String)
    private String topic;

    @ConfigFieldInfo(label = "消息拆分", inputType = FieldInputType.Switch, required = false, defaultValue = "false")
    private boolean messageSharding;
}
