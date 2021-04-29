package io.terminus.dalaran.component.dynamic;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.DalaranDynamicConfig;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.DynamicConfig;
import lombok.Data;

@Data
@DynamicConfig(value = "AuthenticatorTypeTest", name = "AuthenticatorType", type = "Test",  origin = "basic")
public class DynamicAuthenticatorTest implements DalaranDynamicConfig {

    @ConfigFieldInfo(label = "key", inputType = FieldInputType.String)
    private String key;

    @ConfigFieldInfo(label = "value", inputType = FieldInputType.String)
    private String value;
}
