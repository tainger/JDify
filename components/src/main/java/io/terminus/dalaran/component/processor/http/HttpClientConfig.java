package io.terminus.dalaran.component.processor.http;

import io.terminus.dalaran.ConnectorConfig;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.ModelRequiredConfig;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class HttpClientConfig extends ModelRequiredConfig implements ConnectorConfig<HttpClientConnector> {


    @ConfigFieldInfo(label = "连接器", inputType = FieldInputType.Hidden)
    private HttpClientConnector connector;
    
    @ConfigFieldInfo(label = "连接器", inputType = FieldInputType.Connector, connectorType = HttpClientConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String)
    private String path;

    @ConfigFieldInfo(label = "Http Method", inputType = FieldInputType.Select)
    private HttpMethod method;

}