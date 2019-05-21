package io.terminus.dalaran.component.processor.mq;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import io.terminus.dalaran.component.processor.mq.model.MQType;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/17
 */
@Data
public class DalaranMQConnector {

    @ConfigFieldInfo(label = "消息类型", inputType = FieldInputType.CheckBox)
    private MQType type;

    @ConfigFieldInfo(label = "集群地址", inputType = FieldInputType.String)
    private String brokers;
}
