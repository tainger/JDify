package io.terminus.dalaran.component.soap.trigger.model;

import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.component.soap.trigger.SoapListenerConfig;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.flow.TriggerFlow;
import lombok.Data;

import static io.terminus.dalaran.DalaranConstants.MODEL_ROOT;

@Data
public class SoapWordApiInfo {

    private int paramLevel;
    private String name;
    private String moduleName;
    private String description;
    private String path;
    private HttpMethod method;
    private SoapApiParameter input;
    private SoapApiParameter output;
    private Object inExample;
    private Object outExample;
    private MessageModel inSchema;
    private MessageModel outSchema;

    public SoapWordApiInfo(String moduleName, TriggerFlow flow) {
        MessageModel inSchema = flow.getInModel();
        MessageModel outSchema = flow.getOutModel();
        SoapListenerConfig soapListenerConfig = (SoapListenerConfig) flow.getTriggerConfig();
        this.moduleName = moduleName;
        this.name = flow.getName();
        this.description = flow.getDescription();
        this.path = soapListenerConfig.getPath();
        this.method = soapListenerConfig.getMethod();
        this.input = buildApiParam(inSchema);
        this.output = buildApiParam(outSchema);
        this.inSchema = inSchema;
        this.outSchema = outSchema;
    }

    private SoapApiParameter buildApiParam(MessageModel model) {
        ModelField rootField = model.getModelSchema().getFields().get(MODEL_ROOT);
        SoapApiParameter rootParam = new SoapApiParameter();
        rootParam.setDescription(model.getName());
        rootParam.setType(rootField.getType());
        rootParam.setDescription(rootField.getDescription());
        if (!rootField.getType().isBasicType()) {
            rootField.getFields().forEach((name, subField) -> rootParam.getSubParameter().put(name, buildParameters(subField, 1)));
        }
        return rootParam;
    }

    private SoapApiParameter buildParameters(ModelField field, int level) {
        if (level > this.paramLevel) {
            this.paramLevel = level;
        }
        SoapApiParameter param = new SoapApiParameter();
        param.setType(field.getType());
        param.setDescription(field.getDescription());
        switch (field.getType()) {
            case ARRAY: {
                if(field.getSubType()!=null){
                    if (field.getSubType().isBasicType()) {
                        SoapApiParameter subParam = new SoapApiParameter();
                        subParam.setType(field.getSubType());
                        subParam.setDescription(field.getDescription());
                        param.getSubParameter().put("", subParam);
                        break;
                    }
                }
            }
            case OBJECT: {
                field.getFields().forEach((subFieldName, subField) -> {
                    SoapApiParameter subParam = buildParameters(subField, level + 1);
                    param.getSubParameter().put(subFieldName, subParam);
                });
                break;
            }
        }
        return param;
    }
}
