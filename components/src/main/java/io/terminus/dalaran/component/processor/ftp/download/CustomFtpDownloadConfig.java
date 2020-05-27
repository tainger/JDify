package io.terminus.dalaran.component.processor.ftp.download;


import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.FtpUploadConnector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import lombok.Data;

@Data
public class CustomFtpDownloadConfig extends OutModelConfig implements ConnectorConfig<FtpUploadConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    private FtpUploadConnector connector;

    @ConfigFieldInfo(label = "Ftp 连接器", inputType = FieldInputType.Connector, connectorType = FtpUploadConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "执行成功后是否删除文件", inputType = FieldInputType.Switch, defaultValue = "false")
    private Boolean delete;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String path;

    @ConfigFieldInfo(label = "文件名", inputType = FieldInputType.String)
    private String fileName;
}
