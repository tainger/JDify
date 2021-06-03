package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.DalaranBasicComponent;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.DynamicModel;
import lombok.Data;

@Data
@DynamicModel(value = "Connector")
public class BasicConnector implements DalaranBasicComponent {

    @ConfigFieldInfo(label = "组件名称", inputType = FieldInputType.String)
    private String name;

    @ConfigFieldInfo(label = "连接器类型", inputType = FieldInputType.ConnectorSelector, path = "/api/platform/connector", dynamic = true)
    private String connectorType;

    @ConfigFieldInfo(label = "节点", inputType = FieldInputType.NodeSelector, path = "/api/node/list", dynamic = true, required = false)
    private String nodeId;

}
