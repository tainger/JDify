package io.terminus.dalaran.core.flow;

import io.terminus.dalaran.model.flow.FlowValidateMessage;
import io.terminus.dalaran.model.flow.ValidateMessageLevel;

public final class DefaultFlowValidateMessages {


    public static final FlowValidateMessage FIELD_NOT_NULL =
            new FlowValidateMessage(ValidateMessageLevel.Error, "Field.Not.Null", "字段不能为空");
    public static final FlowValidateMessage MODEL_NOT_EQUALLY =
            new FlowValidateMessage(ValidateMessageLevel.Warning, "Model.Not.Equally", "模型不匹配");

}
