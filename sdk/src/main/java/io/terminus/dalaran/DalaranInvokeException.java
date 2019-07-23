package io.terminus.dalaran;

public class DalaranInvokeException extends RuntimeException {

    private String key;

    private String method;

    private int code;

    private String message;

    private String desc;

    public DalaranInvokeException(String key, String method, int code, String message, String desc) {
        super("Call dalaran integration [" + key + ":" + method + "] has error, code[" + code + "], message: " + message + ", description:" + desc);
        this.code = code;
        this.message = message;
    }
}
