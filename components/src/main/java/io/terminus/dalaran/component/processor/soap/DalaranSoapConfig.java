package io.terminus.dalaran.component.processor.soap;

import io.terminus.dalaran.config.OutModelConfig;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/23
 */
@Data
public class DalaranSoapConfig extends OutModelConfig {

    private String wsdl;

    private String portTypeName;

    private String operationName;

    private String bindingName;
}
