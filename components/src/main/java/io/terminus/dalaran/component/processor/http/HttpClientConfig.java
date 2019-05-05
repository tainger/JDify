package io.terminus.dalaran.component.processor.http;

import com.alibaba.fastjson.annotation.JSONField;
import io.terminus.dalaran.ConnectorConfig;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.ModelRequiredConfig;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class HttpClientConfig extends ModelRequiredConfig implements ConnectorConfig<HttpClientConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    private HttpClientConnector connector;

    @ConfigFieldInfo(label = "Http 连接器", inputType = FieldInputType.Connector, connectorType = HttpClientConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "Http Method", inputType = FieldInputType.Select, defaultValue = "GET")
    private HttpMethod method;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String path;

}