package io.terminus.dalaran.component.trigger.rest;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.component.limiter.DalaranLimiter;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.AllModelConfig;
import io.terminus.dalaran.core.component.config.LimiterConfig;
import io.terminus.dalaran.model.HttpProtocol;
import lombok.Data;

@Data
public class RestConfig extends AllModelConfig implements LimiterConfig<DalaranLimiter> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden, required = false)
    @JSONField(serialize = false)
    @JsonIgnore
    private DalaranLimiter limiter;

    @ConfigFieldInfo(label = "限流熔断器", inputType = FieldInputType.Limiter, required = false, connectorType = DalaranLimiter.class)
    private Long limiterId;

    @ConfigFieldInfo(label = "开启限流", inputType = FieldInputType.Switch, defaultValue = "false")
    private boolean enableLimit = false;

    @ConfigFieldInfo(label = "开启熔断", inputType = FieldInputType.Switch, defaultValue = "false")
    private boolean enableBreaker = false;

    // TODO load by application.yml
    @ConfigFieldInfo(label = "端口", inputType = FieldInputType.Hidden)
    private Integer port = 8080;

    @ConfigFieldInfo(label = "开启鉴权", inputType = FieldInputType.Switch, defaultValue = "false")
    private boolean enableSign = false;

    @ConfigFieldInfo(label = "计算签名", inputType = FieldInputType.Switch, defaultValue = "false")
    private boolean checkSign = false;

    @ConfigFieldInfo(label = "密钥", inputType = FieldInputType.Password, required = false)
    private String secret;

    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio, defaultValue = "HTTP")
    private HttpProtocol protocol;

    @ConfigFieldInfo(label = "方法", inputType = FieldInputType.Select, defaultValue = "POST")
    private HttpMethod method;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String path;

    @ConfigFieldInfo(label = "超时时间(ms)", inputType = FieldInputType.Integer, defaultValue = "3000")
    private Long timeout = 3000L;
}
