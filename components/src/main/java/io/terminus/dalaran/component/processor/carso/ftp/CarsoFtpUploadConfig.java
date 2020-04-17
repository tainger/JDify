package io.terminus.dalaran.component.processor.carso.ftp;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.FtpUploadConnector;
import io.terminus.dalaran.component.processor.ftp.FtpUploadConfig;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import lombok.Data;

@Data
public class CarsoFtpUploadConfig extends FtpUploadConfig implements ConnectorConfig<FtpUploadConnector> {

    @ConfigFieldInfo(label = "模板文件路径", inputType = FieldInputType.String, required = false)
    private String templateUri;
}