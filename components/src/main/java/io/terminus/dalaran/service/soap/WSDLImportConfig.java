package io.terminus.dalaran.service.soap;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/27
 */
@Data
public class WSDLImportConfig {

    @ConfigFieldInfo(label = "wsdl url", inputType = FieldInputType.String)
    private String wsdlUrl;
}
