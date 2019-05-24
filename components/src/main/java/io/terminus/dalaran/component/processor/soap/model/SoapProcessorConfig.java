package io.terminus.dalaran.component.processor.soap.model;

import com.predic8.wsdl.Definitions;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/24
 */
@Data
public class SoapProcessorConfig {

    private String wsdl;

    private String portTypeName;

    private String operationName;

    private String bindingName;

    private Definitions definitions;
}
