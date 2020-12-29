package io.terminus.dalaran.component.csb.processor.http;


import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class CsbHttpClientConfig {

    @ConfigFieldInfo(label = "请求URL", inputType = FieldInputType.String, defaultValue = "http://")
    private String requestURL;

    @ConfigFieldInfo(label = "服务名", inputType = FieldInputType.String)
    private String api;

    @ConfigFieldInfo(label = "版本", inputType = FieldInputType.String)
    private String version;

    @ConfigFieldInfo(label = "Http Method", inputType = FieldInputType.Select, defaultValue = "GET")
    private CsbHttpMethod method;

    @ConfigFieldInfo(label = "Headers", inputType = FieldInputType.String, required = false)
    private String Headers;

    @ConfigFieldInfo(label = "accessKey", inputType = FieldInputType.String, required = false)
    private String accessKey;

    @ConfigFieldInfo(label = "secretKey", inputType = FieldInputType.Password, required = false)
    private String secretKey;
}
