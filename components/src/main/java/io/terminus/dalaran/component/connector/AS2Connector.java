package io.terminus.dalaran.component.connector;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.Connector;
import lombok.Data;

@Data
@Connector("AS2")
public class AS2Connector {

    @ConfigFieldInfo(label = "服务地址", inputType = FieldInputType.String)
    private String host;

    @ConfigFieldInfo(label = "端口", inputType = FieldInputType.Integer, defaultValue = "80")
    private Integer port;

    @ConfigFieldInfo(label = "合作伙伴证书", inputType = FieldInputType.String, required = false, defaultValue = "as2/encryption_PGTest.cer")
    private String partnerCertificate;

    @ConfigFieldInfo(label = "集成平台证书", inputType = FieldInputType.String, required = false, defaultValue = "as2/encryption.cer")
    private String stationCertificate;

    @ConfigFieldInfo(label = "集成平台私钥", inputType = FieldInputType.String, required = false, defaultValue = "as2/encryption.pem")
    private String stationPem;

    @ConfigFieldInfo(label = "私钥密码", inputType = FieldInputType.Password, required = false, defaultValue = "anywhere")
    private String password;
}
