package io.terminus.dalaran.message.model;

/**
 * Created by jingdi on 2019/3/12
 */
public class MessageModel {

    private String columnName;

    private String columnType;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }
}
