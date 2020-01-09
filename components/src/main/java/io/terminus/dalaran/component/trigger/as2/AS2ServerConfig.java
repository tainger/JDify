package io.terminus.dalaran.component.trigger.as2;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.AllModelConfig;
import lombok.Data;

@Data
public class AS2ServerConfig extends AllModelConfig {

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String requestUri;

    @ConfigFieldInfo(label = "", inputType = FieldInputType.String, defaultValue = "*")
    private String uriPattern;

    @ConfigFieldInfo(label = "端口", inputType = FieldInputType.Hidden)
    private Integer port = 8080;

    @ConfigFieldInfo(label = "Server Certificate", inputType = FieldInputType.String, required = false, defaultValue = "as2/encryption_PGTest.cer")
    private String partnerCertificate;

    @ConfigFieldInfo(label = "Client Certificate", inputType = FieldInputType.String, required = false, defaultValue = "as2/encryption.cer")
    private String stationCertificate;

    @ConfigFieldInfo(label = "Client Pem", inputType = FieldInputType.String, required = false, defaultValue = "as2/encryption.pem")
    private String stationPem;
}
