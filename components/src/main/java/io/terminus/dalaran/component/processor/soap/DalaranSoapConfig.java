package io.terminus.dalaran.component.processor.soap;

import io.terminus.dalaran.core.component.config.OutModelConfig;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/23
 */
@Data
public class DalaranSoapConfig extends OutModelConfig {

    private String name;

    private String portType;

    private String binding;

    private String input;

    private String outPut;

    private String wsdl;
}
