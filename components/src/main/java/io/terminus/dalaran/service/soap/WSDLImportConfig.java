package io.terminus.dalaran.service.soap;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/27
 */
@Data
public class WSDLImportConfig {

    @ConfigFieldInfo(label = "wsdl url", inputType = FieldInputType.String)
    private String wsdlUrl;

    @ConfigFieldInfo(label = "用户名", inputType = FieldInputType.String, required = false)
    private String username;

    @ConfigFieldInfo(label = "密码", inputType = FieldInputType.String, required = false)
    private String password;
}
