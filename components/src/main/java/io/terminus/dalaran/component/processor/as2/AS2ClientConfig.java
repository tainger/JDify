package io.terminus.dalaran.component.processor.as2;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.AS2Connector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class AS2ClientConfig {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private AS2Connector connector;

    @ConfigFieldInfo(label = "AS2 连接器", inputType = FieldInputType.Connector, connectorType = AS2Connector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String requestUri;

    @ConfigFieldInfo(label = "AS2 Body Type", inputType = FieldInputType.Select, defaultValue = "ediMessage")
    private String bodyType;

    @ConfigFieldInfo(label = "SSL CERTIFICATE", inputType = FieldInputType.String, defaultValue = "")
    private String sslCer;

    @ConfigFieldInfo(label = "SSL PKCS7", inputType = FieldInputType.String, defaultValue = "")
    private String sslPb;

    @ConfigFieldInfo(label = "ENCRYPTION CERTIFICATE", inputType = FieldInputType.String, defaultValue = "")
    private String encryptionCer;

    @ConfigFieldInfo(label = "ENCRYPTION PKCS7", inputType = FieldInputType.String, defaultValue = "")
    private String encryptionPb;

    @ConfigFieldInfo(label = "ISSUE DN", inputType = FieldInputType.String, defaultValue = "")
    private String issueDN;

    @ConfigFieldInfo(label = "SIGNING DN", inputType = FieldInputType.String, defaultValue = "")
    private String signingDN;

}
