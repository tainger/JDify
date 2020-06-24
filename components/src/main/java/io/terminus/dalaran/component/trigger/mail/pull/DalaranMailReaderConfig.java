package io.terminus.dalaran.component.trigger.mail.pull;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.MailConnector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import lombok.Data;

@Data
public class DalaranMailReaderConfig implements ConnectorConfig<MailConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    private MailConnector connector;

    @ConfigFieldInfo(label = "Mail 连接器", inputType = FieldInputType.Connector, connectorType = MailConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "读取后删除邮件", inputType = FieldInputType.Radio, defaultValue = "false")
    private Boolean delete;

    @ConfigFieldInfo(label = "发件人(逗号分隔)", inputType = FieldInputType.String)
    private String readFrom;

    @ConfigFieldInfo(label = "定时器", inputType = FieldInputType.String)
    private String scheduler;

    @ConfigFieldInfo(label = "标题过滤", inputType = FieldInputType.String, required = false)
    private String readSubject;
}
