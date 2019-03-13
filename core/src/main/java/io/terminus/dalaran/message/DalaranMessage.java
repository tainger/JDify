package io.terminus.dalaran.message;

import io.terminus.dalaran.message.model.MessageModel;

/**
 * Created by jingdi on 2019/3/12
 */
public class  DalaranMessage {

    private MessageModel model;

    private String value;

    public MessageModel getModel() {
        return model;
    }

    public void setModel(MessageModel model) {
        this.model = model;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
