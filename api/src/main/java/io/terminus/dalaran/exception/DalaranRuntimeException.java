package io.terminus.dalaran.exception;

public abstract class DalaranRuntimeException extends RuntimeException implements DalaranThrowable {
    private String message;

    public DalaranRuntimeException() {
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }
}
