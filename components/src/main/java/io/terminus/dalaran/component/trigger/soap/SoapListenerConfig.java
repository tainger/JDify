package io.terminus.dalaran.component.trigger.soap;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.trigger.rest.BasicHttpListenerConfig;
import io.terminus.dalaran.component.trigger.soap.model.SoapAuthType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

/**
 * Created by jingdi on 2019/6/13
 */
@Data
public class SoapListenerConfig extends BasicHttpListenerConfig {

    @ConfigFieldInfo(label = "鉴权类型", inputType = FieldInputType.Select, required = false)
    private SoapAuthType authType;

    @ConfigFieldInfo(label = "返回体为空", inputType = FieldInputType.Switch, required = false)
    private boolean nullResponseBody = false;
}
