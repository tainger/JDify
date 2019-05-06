package io.terminus.dalaran;

import io.terminus.dalaran.model.MessageModel;

public interface ModelableConfig {

    MessageModel getInModel();

    void setInModel(MessageModel messageModel);

    Long getInModelId();

    void setInModelId(Long inModelId);

    MessageModel getOutModel();

    void setOutModel(MessageModel messageModel);

    Long getOutModelId();

    void setOutModelId(Long inModelId);
}
