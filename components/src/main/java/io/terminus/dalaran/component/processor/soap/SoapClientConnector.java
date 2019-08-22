package io.terminus.dalaran.component.processor.soap;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.model.HttpProtocol;
import lombok.Data;

/**
 * Created by jingdi on 2019/6/10
 */
@Data
public class SoapClientConnector {

    @ConfigFieldInfo(label = "节点名称", inputType = FieldInputType.String)
    private String name;

    @ConfigFieldInfo(label = "服务地址", inputType = FieldInputType.String)
    private String host;

    @ConfigFieldInfo(label = "端口", inputType = FieldInputType.Integer, defaultValue = "80")
    private Integer port = 80;

    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio, defaultValue = "HTTP")
    private HttpProtocol protocol;

}
