package io.terminus.dalaran.component.processor.soap;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ComponentModelConfig;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import lombok.Data;

/**
 * Created by jingdi on 2019/6/11
 */
@Data
public class SoapClientConfig extends ComponentModelConfig implements ConnectorConfig<SoapClientConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private SoapClientConnector connector;

    @ConfigFieldInfo(label = "SOAP 连接器", inputType = FieldInputType.Connector, connectorType = SoapClientConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "Http Method", inputType = FieldInputType.Select, defaultValue = "POST")
    private HttpMethod method;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String path;
}
