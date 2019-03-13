package io.terminus.dalaran.message;

import io.terminus.dalaran.message.model.ModelType;

import java.util.List;

/**
 * Created by jingdi on 2019/3/13
 */
public class MessageMappingSet {

    private ModelType modelType;

    private List<MessageMapping> mappings;

    public List<MessageMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<MessageMapping> mappings) {
        this.mappings = mappings;
    }

    public ModelType getModelType() {
        return modelType;
    }

    public void setModelType(ModelType modelType) {
        this.modelType = modelType;
    }
}
