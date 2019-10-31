package io.terminus.dalaran.service.soap;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/27
 */
@Data
public class WSDLImportConfig {

    @ConfigFieldInfo(label = "WSDL Url", inputType = FieldInputType.String)
    private String wsdlUrl;

    @ConfigFieldInfo(label = "Username", inputType = FieldInputType.String, required = false)
    private String username;

    @ConfigFieldInfo(label = "Password", inputType = FieldInputType.String, required = false)
    private String password;
}
