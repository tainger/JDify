package io.terminus.dalaran.component.processor.ftp;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.common.FtpUploadConnector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import lombok.Data;

@Data
public class FtpUploadConfig implements ConnectorConfig<FtpUploadConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    private FtpUploadConnector connector;

    @ConfigFieldInfo(label = "Ftp 连接器", inputType = FieldInputType.Connector, connectorType = FtpUploadConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "如果文件已存在", inputType = FieldInputType.Radio, defaultValue = "Override")
    private FileExist fileExist;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String path;

    @ConfigFieldInfo(label = "文件名", inputType = FieldInputType.String)
    private String fileName;
}
