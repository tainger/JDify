package io.terminus.dalaran.message;

import io.terminus.dalaran.message.model.DBMessageModel;
import java.time.LocalDate;

/**
 * Created by jingdi on 2019/3/12
 */
public class DalaranDBMessage {

    private DBMessageModel model;

    private String value;

    public DBMessageModel getModel() {
        return model;
    }

    public void setModel(DBMessageModel model) {
        this.model = model;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private Object parse(String value) {
        switch (model.getType()) {
            case INT:
                return Integer.parseInt(value);
            case VARCHAR:
            case JSON:
            case TEXT:
                return value;
            case FLOAT:
                return Float.valueOf(value);
            case DATE:
                return LocalDate.parse(value);
            default:
                return value;
        }
    }
}