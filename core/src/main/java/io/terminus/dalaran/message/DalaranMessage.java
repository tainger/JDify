package io.terminus.dalaran.message;

import io.terminus.dalaran.message.model.MessageModel;

/**
 * Created by jingdi on 2019/3/12
 */
public class  DalaranMessage {

    private MessageModel model;

    private String type;

    public MessageModel getModel() {
        return model;
    }

    public void setModel(MessageModel model) {
        this.model = model;
    }
}
