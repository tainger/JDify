package io.terminus.dalaran.model;

public class DalaranIntegrationInfo {

    private String key;

    private String method;

    private String name;

    private String description;

    private MessageModel returnType;

    private MessageModel paramType;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MessageModel getReturnType() {
        return returnType;
    }

    public void setReturnType(MessageModel returnType) {
        this.returnType = returnType;
    }

    public MessageModel getParamType() {
        return paramType;
    }

    public void setParamType(MessageModel paramType) {
        this.paramType = paramType;
    }
}
