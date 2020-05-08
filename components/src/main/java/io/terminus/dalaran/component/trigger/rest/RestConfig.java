package io.terminus.dalaran.component.trigger.rest;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.component.trigger.rest.model.EncryptionAlgorithm;
import io.terminus.dalaran.component.trigger.rest.model.SignAlgorithm;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.AllModelConfig;
import io.terminus.dalaran.model.HttpProtocol;
import lombok.Data;

@Data
public class RestConfig extends AllModelConfig {

    // TODO load by application.yml
    @ConfigFieldInfo(label = "端口", inputType = FieldInputType.Hidden)
    private Integer port = 8080;

    @ConfigFieldInfo(label = "鉴权签名", inputType = FieldInputType.Switch, defaultValue = "false")
    private boolean enableSign = false;

    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio, defaultValue = "HTTP")
    private HttpProtocol protocol;

    @ConfigFieldInfo(label = "方法", inputType = FieldInputType.Select, defaultValue = "GET")
    private HttpMethod method;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String path;

    @ConfigFieldInfo(label = "超时时间(ms)", inputType = FieldInputType.Integer, defaultValue = "3000")
    private Long timeout = 3000L;

    @ConfigFieldInfo(label = "平台公钥", inputType = FieldInputType.String, required = false)
    private String dalaranPublicKey;

    @ConfigFieldInfo(label = "平台私钥", inputType = FieldInputType.String, required = false)
    private String dalaranPrivateKey;

    @ConfigFieldInfo(label = "合作伙伴公钥", inputType = FieldInputType.String, required = false)
    private String partnerPublicKey;

    @ConfigFieldInfo(label = "签名算法", inputType = FieldInputType.Select, defaultValue = "SHA256withRSA", required = false)
    private SignAlgorithm signAlgorithm;

    @ConfigFieldInfo(label = "加密算法", inputType = FieldInputType.Select, defaultValue = "RSA", required = false)
    private EncryptionAlgorithm encryptionAlgorithm;
}
