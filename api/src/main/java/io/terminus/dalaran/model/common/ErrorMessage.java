package io.terminus.dalaran.model.common;

import java.util.Map;

public class ErrorMessage {

    private String exceptionType;

    private String message;

    private Map<String, Object> messageData;

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
