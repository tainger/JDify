package io.terminus.dalaran.component.processor.sgm;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import lombok.Data;

@Data
public class SGMHttpClientConfig extends OutModelConfig implements ConnectorConfig<SGMHttpClientConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private SGMHttpClientConnector connector;

    @ConfigFieldInfo(label = "Http 连接器", inputType = FieldInputType.Connector, connectorType = SGMHttpClientConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "命令", inputType = FieldInputType.String, defaultValue = "1000")
    private String command;
}
