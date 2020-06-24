package io.terminus.dalaran.component.connector;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.common.MailProtocol;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.Connector;
import lombok.Data;

@Data
@Connector("Mail")
public class MailConnector {

    @ConfigFieldInfo(label = "邮箱地址", inputType = FieldInputType.String)
    private String host;

    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio, defaultValue = "IMAP")
    private MailProtocol protocol;

    @ConfigFieldInfo(label = "用户名", inputType = FieldInputType.String)
    private String username;

    @ConfigFieldInfo(label = "密码", inputType = FieldInputType.Password)
    private String password;
}
