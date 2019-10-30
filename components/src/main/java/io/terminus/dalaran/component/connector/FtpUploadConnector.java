package io.terminus.dalaran.component.connector;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.common.FtpProtocol;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.Connector;
import lombok.Data;

@Data
@Connector("Ftp")
public class FtpUploadConnector {
    @ConfigFieldInfo(label = "服务地址", inputType = FieldInputType.String)
    private String host;

    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio, defaultValue = "FTP")
    private FtpProtocol protocol;

    @ConfigFieldInfo(label = "端口", inputType = FieldInputType.Integer, defaultValue = "21")
    private Integer port = 80;

    @ConfigFieldInfo(label = "超时时间", inputType = FieldInputType.Integer, defaultValue = "3000")
    private Long timeout = 3000L;

    @ConfigFieldInfo(label = "用户名", inputType = FieldInputType.String, required = false)
    private String username;

    @ConfigFieldInfo(label = "密码", inputType = FieldInputType.String, required = false)
    private String password;
}
