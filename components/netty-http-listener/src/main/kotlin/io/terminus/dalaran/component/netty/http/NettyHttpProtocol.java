package io.terminus.dalaran.component.netty.http;

import io.terminus.dalaran.DalaranConfigEnum;

public enum NettyHttpProtocol implements DalaranConfigEnum {

    HTTP("http"),
    HTTPS("https");

    private String displayName;
    private String value;

    NettyHttpProtocol(String value) {
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
