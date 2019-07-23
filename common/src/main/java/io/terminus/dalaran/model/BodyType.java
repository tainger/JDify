package io.terminus.dalaran.model;

/**
 * Created by jingdi on 2019/3/13
 */
public enum BodyType {

    JSON(true),
    XML(true),
    OBJECT(false),
    SOAP(true),
    EXCEPTION(false);

    private boolean serialized;


    BodyType(boolean serialized) {
        this.serialized = serialized;
    }

    public boolean isSerialized() {
        return serialized;
    }
}
