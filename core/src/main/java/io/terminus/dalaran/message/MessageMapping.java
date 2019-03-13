package io.terminus.dalaran.message;

import io.terminus.dalaran.message.model.MessageModel;

/**
 * Created by jingdi on 2019/3/12
 */
public class MessageMapping {

    private MessageModel targetModel;

    private MessageModel destinationModel;

    private MessageProcessFunction function;

    public MessageModel getTargetModel() {
        return targetModel;
    }

    public void setTargetModel(MessageModel targetModel) {
        this.targetModel = targetModel;
    }

    public MessageModel getDestinationModel() {
        return destinationModel;
    }

    public void setDestinationModel(MessageModel destinationModel) {
        this.destinationModel = destinationModel;
    }

    public MessageProcessFunction getFunction() {
        return function;
    }

    public void setFunction(MessageProcessFunction function) {
        this.function = function;
    }
}
