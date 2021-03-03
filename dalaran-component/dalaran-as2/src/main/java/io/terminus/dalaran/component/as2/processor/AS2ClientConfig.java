package io.terminus.dalaran.component.as2.processor;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.AS2Connector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import lombok.Data;

@Data
public class AS2ClientConfig extends OutModelConfig implements ConnectorConfig<AS2Connector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private AS2Connector connector;

    @ConfigFieldInfo(label = "AS2 连接器", inputType = FieldInputType.Connector, connectorType = AS2Connector.class)
    private String connectorId;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String requestUri;

//    @ConfigFieldInfo(label = "AS2 Body Type", inputType = FieldInputType.String, defaultValue = "ediMessage")
    private String bodyType = "ediMessage";
}
