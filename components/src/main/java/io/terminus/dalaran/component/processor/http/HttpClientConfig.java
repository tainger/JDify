package io.terminus.dalaran.component.processor.http;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.component.connector.RestClientConnector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import lombok.Data;

@Data
public class HttpClientConfig extends OutModelConfig implements ConnectorConfig<RestClientConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private RestClientConnector connector;

    @ConfigFieldInfo(label = "Http 连接器", inputType = FieldInputType.Connector, connectorType = RestClientConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "Http Method", inputType = FieldInputType.Select, defaultValue = "GET")
    private HttpMethod method;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String path;

    @ConfigFieldInfo(label = "Headers", inputType = FieldInputType.String, required = false)
    private String headers;

    @ConfigFieldInfo(label = "Query Param", inputType = FieldInputType.String, required = false)
    private String queryParams;
}