package io.terminus.dalaran.component.csb.processor.webservice;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class WebServiceClientConfig {

    @ConfigFieldInfo(label = "nameSpace", inputType = FieldInputType.String, defaultValue = "http://")
    private String nameSpace;

    @ConfigFieldInfo(label = "serviceName", inputType = FieldInputType.String)
    private String serviceName;

    @ConfigFieldInfo(label = "portName", inputType = FieldInputType.String)
    private String portName;

    @ConfigFieldInfo(label = "soapActionUri", inputType = FieldInputType.String, defaultValue = "http://")
    private String soapActionUri;

    @ConfigFieldInfo(label = "endpoint", inputType = FieldInputType.String)
    private String endpoint;

    @ConfigFieldInfo(label = "服务名", inputType = FieldInputType.String)
    private String api;

    @ConfigFieldInfo(label = "版本", inputType = FieldInputType.String)
    private String version;

    @ConfigFieldInfo(label = "accessKey", inputType = FieldInputType.String, required = false)
    private String accessKey;

    @ConfigFieldInfo(label = "secretKey", inputType = FieldInputType.Password, required = false)
    private String secretKey;

}
