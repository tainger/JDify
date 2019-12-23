package io.terminus.dalaran.component.trigger.as2;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.AS2Connector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class AS2ServerConfig {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private AS2Connector connector;

    @ConfigFieldInfo(label = "AS2 连接器", inputType = FieldInputType.Connector, connectorType = AS2Connector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String uriPattern;
}
