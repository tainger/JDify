package io.terminus.dalaran.exception;

public class DalaranException extends Exception implements DalaranThrowable {

    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.getClass().getSimpleName();
    }
}