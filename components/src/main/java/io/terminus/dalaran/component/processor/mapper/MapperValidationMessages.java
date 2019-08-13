package io.terminus.dalaran.component.processor.mapper;

import io.terminus.dalaran.model.flow.FlowValidateMessage;
import io.terminus.dalaran.model.flow.ValidateMessageLevel;

public final class MapperValidationMessages {

    protected static final FlowValidateMessage MAPPER_FUNCTION_IRREGULAR =
            new FlowValidateMessage(ValidateMessageLevel.Warning, "Mapper.Function.Irregular", "");

    protected static final FlowValidateMessage MAPPER_FUNCTION_PARAM_NOT_NULL =
            new FlowValidateMessage(ValidateMessageLevel.Warning, "Mapper.Function.Param.Not.Null", "");

    protected static final FlowValidateMessage MAPPER_SOURCE_PATH_NOT_NULL =
            new FlowValidateMessage(ValidateMessageLevel.Warning, "Mapper.Function.SourcePath.Not.Null", "");

    protected static final FlowValidateMessage MODEL_NOT_NULL =
            new FlowValidateMessage(ValidateMessageLevel.Error, "InModel.Or.OutModel.Not.Null", "");

    protected static final FlowValidateMessage MAPPER_ARRAY_LEVEL_NOT_EQUALS =
            new FlowValidateMessage(ValidateMessageLevel.Warning, "Mapper.Array.Level.Not.Equals", "");

    protected static final FlowValidateMessage PATH_NOT_IN_MODEL =
            new FlowValidateMessage(ValidateMessageLevel.Error, "Path.Not.In.Model", "");

}
