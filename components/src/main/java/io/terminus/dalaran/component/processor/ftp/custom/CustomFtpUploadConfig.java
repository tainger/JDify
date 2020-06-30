package io.terminus.dalaran.component.processor.ftp.custom;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.FtpConnector;
import io.terminus.dalaran.component.processor.ftp.common.FtpUploadConfig;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import lombok.Data;

@Data
public class CustomFtpUploadConfig extends FtpUploadConfig implements ConnectorConfig<FtpConnector> {

    @ConfigFieldInfo(label = "模板文件路径", inputType = FieldInputType.FileUpload, required = false)
    private String templateUri;
}