package io.terminus.dalaran.component.trigger.ftp;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.FtpUploadConnector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.component.config.InModelConfig;
import lombok.Data;

@Data
public class FtpDownloadConfig extends InModelConfig implements ConnectorConfig<FtpUploadConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    private FtpUploadConnector connector;

    @ConfigFieldInfo(label = "Ftp 连接器", inputType = FieldInputType.Connector, connectorType = FtpUploadConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "执行成功后是否删除文件", inputType = FieldInputType.Switch, defaultValue = "false")
    private Boolean delete;

    @ConfigFieldInfo(label = "拉取规则(Cron 表达式)", inputType = FieldInputType.String)
    private String cron;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String path;

    @ConfigFieldInfo(label = "文件名, 如果为空则拉取路径下所有文件", inputType = FieldInputType.String, required = false)
    private String fileName;

    @ConfigFieldInfo(label = "", inputType = FieldInputType.Switch, defaultValue = "false")
    private boolean fuzzyMatching = false;
}
