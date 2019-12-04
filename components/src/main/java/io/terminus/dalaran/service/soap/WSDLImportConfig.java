package io.terminus.dalaran.service.soap;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

import java.util.Map;

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

    //    @ConfigFieldInfo(label = "使用头信息", inputType = FieldInputType.Switch, required = false)
    private Boolean useHeader = false;

//    @ConfigFieldInfo(label = "头信息配置", inputType = FieldInputType.Auto, required = false)
    private Map<String, Object> headerValues;
}
