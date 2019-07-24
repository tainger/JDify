package io.terminus.dalaran;

public class DalaranInvokeException extends RuntimeException {

    private String key;

    private String method;

    private int code;

    private String invokeMessage;

    private String description;

    public DalaranInvokeException(String key, String method, int code, String invokeMessage, String description) {
        super("Call dalaran integration [" + key + ":" + method + "] has error, code[" + code + "], message: " + invokeMessage + ", description:" + description);
        this.key = key;
        this.method = method;
        this.code = code;
        this.invokeMessage = invokeMessage;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getMethod() {
        return method;
    }

    public int getCode() {
        return code;
    }

    public String getInvokeMessage() {
        return invokeMessage;
    }

    public String getDescription() {
        return description;
    }
}
