package io.terminus.dalaran.component.http.processor.okhttp;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.SourceType;
import io.terminus.dalaran.component.common.ContentType;
import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.component.connector.RestClientConnector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import lombok.Data;

@Data
public class OKHttpClientConfig extends OutModelConfig implements ConnectorConfig<RestClientConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private RestClientConnector connector;

    @ConfigFieldInfo(label = "Http 连接器", inputType = FieldInputType.Connector,
            connectorType = RestClientConnector.class, sourceType = SourceType.CONNECTOR)
    private String connectorId;

    @ConfigFieldInfo(label = "Http Method", inputType = FieldInputType.Select, defaultValue = "GET")
    private HttpMethod method;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String path;

    @ConfigFieldInfo(label = "数据类型", inputType = FieldInputType.Select, defaultValue = "APPLICATION_JSON")
    private ContentType contentType = ContentType.APPLICATION_JSON;

    @ConfigFieldInfo(label = "Headers", inputType = FieldInputType.String, required = false)
    private String headers;

    @ConfigFieldInfo(label = "Query Param", inputType = FieldInputType.String, required = false)
    private String queryParams;

    @ConfigFieldInfo(label = "Form Data", inputType = FieldInputType.String, required = false)
    private String formData;

//    @ConfigFieldInfo(label = "Add Last Headers", inputType = FieldInputType.Switch, required = false)
//    private Boolean addLastHeaders = false;

    @ConfigFieldInfo(label = "是否校验证书", inputType = FieldInputType.Switch, required = false)
    private Boolean checkCertificate = true;

    @ConfigFieldInfo(label = "SSL Certificate", inputType = FieldInputType.FileUpload, required = false)
    private String sslCertificate;
}
