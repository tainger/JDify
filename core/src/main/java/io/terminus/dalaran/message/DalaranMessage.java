package io.terminus.dalaran.message;

import io.terminus.dalaran.message.model.MessageModel;
import io.terminus.dalaran.message.model.ModelType;

import java.util.List;

/**
 * Created by jingdi on 2019/3/12
 */
public class  DalaranMessage {

    private List<MessageModel> fields;

    private ModelType type;

    public List<MessageModel> getFields() {
        return fields;
    }

    public void setFields(List<MessageModel> fields) {
        this.fields = fields;
    }

    public ModelType getType() {
        return type;
    }

    public void setType(ModelType type) {
        this.type = type;
    }
}
