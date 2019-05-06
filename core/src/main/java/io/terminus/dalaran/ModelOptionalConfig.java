package io.terminus.dalaran;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import io.terminus.dalaran.model.MessageModel;
import lombok.Data;

@Data
public class ModelOptionalConfig implements ModelableConfig {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private transient MessageModel inModel;

    @ConfigFieldInfo(label = "输入结构", inputType = FieldInputType.Model, required = false)
    private Long inModelId;

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private transient MessageModel outModel;

    @ConfigFieldInfo(label = "输出结构", inputType = FieldInputType.Model, required = false)
    private Long outModelId;
}
