package io.terminus.dalaran.component.common.exception;

import lombok.Data;

@Data
public class FieldParseException extends RuntimeException {

    private String fieldPath;

    private Object fieldValue;

    private String message;

    public FieldParseException(String fieldPath, Object fieldValue, String message) {
        super(message);
        this.fieldPath = fieldPath;
        this.fieldValue = fieldValue;
        this.message = message;
    }
}
