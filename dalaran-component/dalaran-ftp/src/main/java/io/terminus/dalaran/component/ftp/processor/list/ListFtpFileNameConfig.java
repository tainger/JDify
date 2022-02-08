package io.terminus.dalaran.component.ftp.processor.list;


import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.FtpConnector;
import io.terminus.dalaran.component.ftp.processor.FileNameConnector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import lombok.Data;

@Data
public class ListFtpFileNameConfig extends OutModelConfig implements ConnectorConfig<FtpConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    private FtpConnector connector;

    @ConfigFieldInfo(label = "Ftp 连接器", inputType = FieldInputType.Connector, connectorType = FtpConnector.class)
    private String connectorId;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String path;
}
