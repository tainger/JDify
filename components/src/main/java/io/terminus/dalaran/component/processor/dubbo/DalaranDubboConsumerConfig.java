package io.terminus.dalaran.component.processor.dubbo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.DubboRegistryConnector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import lombok.Data;

@Data
public class DalaranDubboConsumerConfig extends OutModelConfig implements ConnectorConfig<DubboRegistryConnector> {

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
    @ConfigFieldInfo(label = "超时时间(ms)", inputType = FieldInputType.Integer, defaultValue = "500")
    private Integer timeout = 500;
    @ConfigFieldInfo(label = "重试次数", inputType = FieldInputType.Integer, defaultValue = "3")
    private Integer retries = 3;
    @ConfigFieldInfo(label = "入参类型(有方法重载时才需要声明入参类型)", inputType = FieldInputType.String, required = false)
    private String parameterType;

}
