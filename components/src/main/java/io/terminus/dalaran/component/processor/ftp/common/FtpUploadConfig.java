package io.terminus.dalaran.component.processor.ftp.common;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.FtpConnector;
import io.terminus.dalaran.component.processor.ftp.FileExist;
import io.terminus.dalaran.component.processor.ftp.FtpInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import lombok.Data;

@Data
public class FtpUploadConfig implements ConnectorConfig<FtpConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    private FtpConnector connector;

    @ConfigFieldInfo(label = "Ftp 连接器", inputType = FieldInputType.Connector, connectorType = FtpConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "如果文件已存在", inputType = FieldInputType.Select, defaultValue = "Override")
    private FileExist fileExist;

    @ConfigFieldInfo(label = "动态文件名", inputType = FieldInputType.Switch, defaultValue = "false")
    private boolean dynamicFileName = false;

    @ConfigFieldInfo(label = "入参类型", inputType = FieldInputType.Select, defaultValue = "OBJECT")
    private FtpInputType inputType = FtpInputType.OBJECT;

    @ConfigFieldInfo(label = "动态路径", inputType = FieldInputType.String, defaultValue = "", required = false)
    private String dynamicPath;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/", required = false)
    private String path;

    @ConfigFieldInfo(label = "文件前缀", inputType = FieldInputType.String, required = false)
    private String fileRoot;

    @ConfigFieldInfo(label = "文件后缀日期格式", inputType = FieldInputType.String, required = false)
    private String datePattern;

    @ConfigFieldInfo(label = "文件后缀", inputType = FieldInputType.String, required = false)
    private String fileSuffix;
}
