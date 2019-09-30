package io.terminus.dalaran.component.processor.rocketmq;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;
import org.apache.camel.spi.UriParam;

/**
 * Created by jingdi on 2019/7/2
 */
@Data
public class RocketMQConnector {

    @ConfigFieldInfo(label = "接入点", inputType = FieldInputType.String)
    private String nameServer;

    @ConfigFieldInfo(label = "是否使用阿里云 ONS", inputType = FieldInputType.Switch, defaultValue = "false")
    private Boolean useAliCloudOns = false;

    @ConfigFieldInfo(label = "ACL accessKey", inputType = FieldInputType.String, required = false)
    private String accessKey;

    @ConfigFieldInfo(label = "ACL secretKey", inputType = FieldInputType.String, required = false)
    private String secretKey;
}
