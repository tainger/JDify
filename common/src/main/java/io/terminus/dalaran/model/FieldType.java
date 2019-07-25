package io.terminus.dalaran.model;

/**
 * Created by jingdi on 2019/3/25
 */
public enum FieldType {
    STRING, INTEGER, LONG, SHORT, FLOAT, DOUBLE, DATE, BOOLEAN, NUMBER, ARRAY(false), MAP(false), OBJECT(false);

    private boolean basicType;

    FieldType() {
        this(true);
    }

    FieldType(boolean basicType) {
        this.basicType = basicType;
    }

    public boolean isBasicType() {
        return basicType;
    }
}
