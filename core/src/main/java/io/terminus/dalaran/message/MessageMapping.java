package io.terminus.dalaran.message;

import io.terminus.dalaran.message.model.MessageModel;

/**
 * Created by jingdi on 2019/3/12
 */
public class MessageMapping {

    private MessageModel originModel;

    private MessageModel targetModel;

    private MessageProcessFunction function;

    public MessageModel getOriginModel() {
        return originModel;
    }

    public void setOriginModel(MessageModel originModel) {
        this.originModel = originModel;
    }

    public MessageModel getTargetModel() {
        return targetModel;
    }

    public void setTargetModel(MessageModel targetModel) {
        this.targetModel = targetModel;
    }

    public MessageProcessFunction getFunction() {
        return function;
    }

    public void setFunction(MessageProcessFunction function) {
        this.function = function;
    }
}
