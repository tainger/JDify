package io.terminus.dalaran.core.component.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.model.MessageModel;
import lombok.Data;

@Data
public class ImmutableInModelConfig {

    @ConfigFieldInfo(label = "输入结构", inputType = FieldInputType.Model, readonly = true)
    private String inModelId;

    @ConfigFieldInfo(label = "输出结构", inputType = FieldInputType.Model)
    private String outModelId;

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private transient MessageModel inModel;

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private transient MessageModel outModel;
}
