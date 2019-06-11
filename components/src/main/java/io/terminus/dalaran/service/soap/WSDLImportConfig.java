package io.terminus.dalaran.service.soap;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/27
 */
@Data
public class WSDLImportConfig {

    @ConfigFieldInfo(label = "wsdl url", inputType = FieldInputType.String)
    private String wsdlUrl;
}
