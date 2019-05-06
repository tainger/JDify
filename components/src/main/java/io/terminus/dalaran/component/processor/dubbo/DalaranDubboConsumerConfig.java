package io.terminus.dalaran.component.processor.dubbo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.ConnectorConfig;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.ModelRequiredConfig;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import io.terminus.dalaran.component.common.DubboRegistryConnector;
import lombok.Data;

@Data
public class DalaranDubboConsumerConfig extends ModelRequiredConfig implements ConnectorConfig<DubboRegistryConnector> {

    @ConfigFieldInfo(label = "注册中心", inputType = FieldInputType.Connector, connectorType = DubboRegistryConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private DubboRegistryConnector connector;

    @ConfigFieldInfo(label = "服务 ID", inputType = FieldInputType.String)
    private String serviceId;
    @ConfigFieldInfo(label = "服务方法", inputType = FieldInputType.String)
    private String method;
    @ConfigFieldInfo(label = "服务版本", inputType = FieldInputType.String)
    private String version;

}
