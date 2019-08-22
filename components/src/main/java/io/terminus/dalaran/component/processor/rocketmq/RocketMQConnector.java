package io.terminus.dalaran.component.processor.rocketmq;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

/**
 * Created by jingdi on 2019/7/2
 */
@Data
public class RocketMQConnector {

    @ConfigFieldInfo(label = "节点名称", inputType = FieldInputType.String)
    private String name;

    @ConfigFieldInfo(label = "注册中心", inputType = FieldInputType.String)
    private String nameServer;
}
