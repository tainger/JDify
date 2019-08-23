package io.terminus.dalaran.core.component.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.model.MessageModel;
import lombok.Data;

@Data
public class ImmutableModelConfig {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden, readonly = true)
    @JSONField(serialize = false)
    @JsonIgnore
    private transient MessageModel inModel;

    @ConfigFieldInfo(inputType = FieldInputType.Hidden, readonly = true)
    @JSONField(serialize = false)
    @JsonIgnore
    private transient MessageModel outModel;
}
