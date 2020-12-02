package io.terminus.dalaran.component.connector;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.Connector;
import lombok.Data;

@Data
@Connector("Dubbo")
public class DubboRegistryConnector {

    @ConfigFieldInfo(label = "注册中心地址", inputType = FieldInputType.String, defaultValue = "zookeeper://ZOOKEEPER_HOST:ZOOKEEPER_PORT")
    private String address;

    @ConfigFieldInfo(label = "应用名", inputType = FieldInputType.String)
    private String application;

    @ConfigFieldInfo(label = "线程池", inputType = FieldInputType.Integer)
    private Integer threads = 500;
}
