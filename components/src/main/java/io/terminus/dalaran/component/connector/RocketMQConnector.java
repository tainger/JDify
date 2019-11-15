package io.terminus.dalaran.component.connector;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.Connector;
import lombok.Data;

/**
 * Created by jingdi on 2019/7/2
 */
@Data
@Connector("RocketMQ")
public class RocketMQConnector {

    @ConfigFieldInfo(label = "接入点", inputType = FieldInputType.String)
    private String nameServer;

    @ConfigFieldInfo(label = "是否使用阿里云 ONS", inputType = FieldInputType.Switch, defaultValue = "false")
    private Boolean useAliCloudOns = false;

    @ConfigFieldInfo(label = "ACL accessKey", inputType = FieldInputType.String, required = false)
    private String accessKey;

    @ConfigFieldInfo(label = "ACL secretKey", inputType = FieldInputType.String, required = false)
    private String secretKey;

    @ConfigFieldInfo(label = "超时时间", inputType = FieldInputType.String, required = false)
    private Integer timeout = 3000;
}
