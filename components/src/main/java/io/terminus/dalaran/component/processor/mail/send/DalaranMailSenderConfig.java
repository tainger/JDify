package io.terminus.dalaran.component.processor.mail.send;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.MailConnector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class DalaranMailSenderConfig {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    private MailConnector connector;

    @ConfigFieldInfo(label = "Mail 连接器", inputType = FieldInputType.Connector, connectorType = MailConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "收件人(逗号分隔)", inputType = FieldInputType.Radio, defaultValue = "false")
    private Boolean dynamicAddress;

    @ConfigFieldInfo(label = "收件人(逗号分隔)", inputType = FieldInputType.String, required = false)
    private String sendTo;

    @ConfigFieldInfo(label = "抄送人(逗号分隔)", inputType = FieldInputType.String, required = false)
    private String ccTo;

    @ConfigFieldInfo(label = "标题", inputType = FieldInputType.String, required = false)
    private String subject;
}
