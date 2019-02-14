package io.terminus.dalaran.component.http.request;

import io.terminus.dalaran.DalaranConfigEnum;

public enum HttpRequestProtocol implements DalaranConfigEnum {

    HTTP("http"),
    HTTPS("https");

    private String displayName;
    private String value;

    HttpRequestProtocol(String value) {
        this.value = value;
    }

    @Override
    public String getDisplayName() {
        return name();
    }

    @Override
    public String getValue() {
        return value;
    }
}
