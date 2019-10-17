package io.terminus.dalaran.exception;

import io.terminus.dalaran.model.common.ErrorMessage;

public class DalaranException extends Exception implements DalaranThrowable {

    private String message;

    public DalaranException() {
    }

    public DalaranException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setErrorMessage(ErrorMessage errorMessage) {
        this.message = errorMessage.getMessage();
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }
}