package io.terminus.dalaran;

import io.terminus.dalaran.annotation.ConfigFieldInfo;
import io.terminus.dalaran.model.MessageModel;
import lombok.Data;

@Data
public class ModelRequiredConfig implements ModelableConfig {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    private MessageModel inModel;

    @ConfigFieldInfo(label = "输入结构", inputType = FieldInputType.Model)
    private Long inModelId;

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    private MessageModel outModel;

    @ConfigFieldInfo(label = "输出结构", inputType = FieldInputType.Model)
    private Long outModelId;
}
