package io.terminus.dalaran.component.dynamic;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.DalaranDynamicConfig;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.DynamicConfig;
import lombok.Data;

@Data
@DynamicConfig(value = "OKHttpClientCheckCertificate", name = "OKHttpClientCheckCertificate", type = "switch", origin = "basic")
public class DynamicOKHttpClientCheck implements DalaranDynamicConfig {

    @ConfigFieldInfo(label = "SSL Certificate", inputType = FieldInputType.FileUpload, required = false)
    private String sslCertificate;

}
