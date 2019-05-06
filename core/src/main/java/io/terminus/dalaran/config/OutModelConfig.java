package io.terminus.dalaran.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import io.terminus.dalaran.model.MessageModel;
import lombok.Data;

@Data
public class OutModelConfig {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private transient MessageModel outModel;

    @ConfigFieldInfo(label = "输出结构", inputType = FieldInputType.Model, required = false)
    private Long outModelId;
}
