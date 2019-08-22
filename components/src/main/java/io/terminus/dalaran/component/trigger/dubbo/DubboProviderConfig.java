package io.terminus.dalaran.component.trigger.dubbo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.component.common.DubboRegistryConnector;
import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.AllModelConfig;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import lombok.Data;

@Data
public class DubboProviderConfig extends AllModelConfig implements ConnectorConfig<DubboRegistryConnector> {

    @ConfigFieldInfo(label = "节点名称", inputType = FieldInputType.String)
    private String name;

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