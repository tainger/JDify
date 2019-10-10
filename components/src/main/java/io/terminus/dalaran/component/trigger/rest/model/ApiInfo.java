package io.terminus.dalaran.component.trigger.rest.model;

import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.component.trigger.rest.RestConfig;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.flow.TriggerFlow;
import lombok.Data;

import static io.terminus.dalaran.DalaranConstants.MODEL_ROOT;

//TODO 写到这有点奇怪...
@Data
public class ApiInfo {
    private int paramLevel;
    private String name;
    private String moduleName;
    private String description;
    private String path;
    private HttpMethod method;
    private ApiParameter input;
    private ApiParameter output;

    public ApiInfo(String moduleName, TriggerFlow flow) {
        MessageModel inSchema = flow.getInModel();
        MessageModel outSchema = flow.getInModel();
        RestConfig restConfig = (RestConfig) flow.getTriggerConfig();
        this.moduleName = moduleName;
        this.name = flow.getName();
        this.description = flow.getDescription();
        this.path = restConfig.getPath();
        this.method = restConfig.getMethod();
        this.input = buildApiParam(inSchema);
        this.output = buildApiParam(outSchema);
    }

    private ApiParameter buildApiParam(MessageModel model) {
        ModelField rootField = model.getModelSchema().getFields().get(MODEL_ROOT);
        ApiParameter rootParam = new ApiParameter();
        rootParam.setDescription(model.getName());
        rootParam.setType(rootField.getType());
        rootParam.setDescription(rootField.getDescription());
        if (!rootField.getType().isBasicType()) {
            rootField.getFields().forEach((name, subField) -> rootParam.getSubParameter().put(name, buildParameters(subField, 1)));
        }
        return rootParam;
    }

    private ApiParameter buildParameters(ModelField field, int level) {
        if (level > this.paramLevel) {
            this.paramLevel = level;
        }
        ApiParameter param = new ApiParameter();
        param.setType(field.getType());
        param.setDescription(field.getDescription());
        switch (field.getType()) {
            case ARRAY: {
                if (field.getSubType().isBasicType()) {
                    ApiParameter subParam = new ApiParameter();
                    subParam.setType(field.getSubType());
                    subParam.setDescription(field.getDescription());
                    param.getSubParameter().put("", subParam);
                    break;
                }
            }
            case OBJECT: {
                field.getFields().forEach((subFieldName, subField) -> {
                    ApiParameter subParam = buildParameters(subField, level + 1);
                    param.getSubParameter().put(subFieldName, subParam);
                });
                break;
            }
        }
        return param;
    }
}
