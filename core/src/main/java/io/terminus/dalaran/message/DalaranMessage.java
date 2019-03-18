package io.terminus.dalaran.message;

import java.util.Map;

/**
 * Created by jingdi on 2019/3/12
 */
public class  DalaranMessage {

    private Map<String, Object> fields;

    private ModelType type;

    public DalaranMessage(Map<String, Object> fields, ModelType type) {
        this.fields = fields;
        this.type = type;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public ModelType getType() {
        return type;
    }

    public void setType(ModelType type) {
        this.type = type;
    }
}
