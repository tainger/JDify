package io.terminus.dalaran.message.model;

import io.terminus.dalaran.message.DalaranDBMessage;

/**
 * Created by jingdi on 2019/3/12
 */
public class DBMessageModel extends MessageModel {

    private ModelType type;

    public enum ModelType {
        INT, VARCHAR, DATE, JSON, FLOAT, TEXT, BLOB;

    }

    public ModelType getType() {
        return type;
    }

    public void setType(ModelType type) {
        this.type = type;
    }



}
