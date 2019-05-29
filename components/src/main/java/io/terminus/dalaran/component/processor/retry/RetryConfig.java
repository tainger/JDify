package io.terminus.dalaran.component.processor.retry;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ComponentModelConfig;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import lombok.Data;

import java.util.List;

@Data
public class RetryConfig {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private String fragmentUri;

    @ConfigFieldInfo(label = "最大重试次数", inputType = FieldInputType.Integer)
    private Integer maxRetry;

    @ConfigFieldInfo(label = "重试间隔(ms)", inputType = FieldInputType.Integer)
    private Integer retryDelay;

    @ConfigFieldInfo(inputType = FieldInputType.Pipeline)
    private List<ProcessorEntity> pipeline;
}
