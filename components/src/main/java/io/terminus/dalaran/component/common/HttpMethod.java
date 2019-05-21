package io.terminus.dalaran.component.common;

public enum HttpMethod {
    GET(true), DELETE(true), OPTIONS(true), HEAD(true), CONNECT(true),
    POST(false), PATCH(false), PUT(false), TRACE(false);

    private boolean noBody;

    HttpMethod(boolean noBody) {
        this.noBody = noBody;
    }

    public boolean isNoBody() {
        return noBody;
    }
}
