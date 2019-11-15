package io.terminus.dalaran.component.connector;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.Connector;
import io.terminus.dalaran.model.HttpProtocol;
import lombok.Data;

/**
 * Created by jingdi on 2019/6/10
 */
@Data
@Connector("SOAP")
public class SoapClientConnector {

    @ConfigFieldInfo(label = "服务地址", inputType = FieldInputType.String)
    private String host;

    @ConfigFieldInfo(label = "端口", inputType = FieldInputType.Integer, defaultValue = "80")
    private Integer port = 80;

    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio, defaultValue = "HTTP")
    private HttpProtocol protocol;

    @ConfigFieldInfo(label = "用户名", inputType = FieldInputType.String, required = false)
    private String username;

    @ConfigFieldInfo(label = "密码", inputType = FieldInputType.String, required = false)
    private String password;
}
