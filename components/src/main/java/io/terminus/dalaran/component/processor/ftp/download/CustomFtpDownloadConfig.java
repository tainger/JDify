package io.terminus.dalaran.component.processor.ftp.download;


import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.FtpConnector;
import io.terminus.dalaran.component.processor.ftp.FileNameConnector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import lombok.Data;

@Data
public class CustomFtpDownloadConfig extends OutModelConfig implements ConnectorConfig<FtpConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    private FtpConnector connector;

    @ConfigFieldInfo(label = "Ftp 连接器", inputType = FieldInputType.Connector, connectorType = FtpConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "执行成功后是否删除文件", inputType = FieldInputType.Switch, defaultValue = "false")
    private Boolean delete;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String path;

    @ConfigFieldInfo(label = "动态文件名", inputType = FieldInputType.Switch, defaultValue = "false")
    private boolean dynamicFileName = false;

    @ConfigFieldInfo(label = "动态路径", inputType = FieldInputType.String, defaultValue = "", required = false)
    private String dynamicPath;

    @ConfigFieldInfo(label = "文件名前缀", inputType = FieldInputType.String)
    private String fileName;

    @ConfigFieldInfo(label = "前缀日期分隔符", inputType = FieldInputType.Select, required = false)
    private FileNameConnector dateConnector = FileNameConnector.DOT;

    @ConfigFieldInfo(label = "日期格式", inputType = FieldInputType.String, required = false)
    private String datePattern;

    @ConfigFieldInfo(label = "文件名后缀", inputType = FieldInputType.String, required = false)
    private String fileSuffix;

}
