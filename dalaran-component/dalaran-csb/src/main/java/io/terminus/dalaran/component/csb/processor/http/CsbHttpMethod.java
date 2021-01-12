package io.terminus.dalaran.component.csb.processor.http;

public enum  CsbHttpMethod {
    GET(true),POST(false);

    private boolean noBody;

    CsbHttpMethod(boolean noBody) {
        this.noBody = noBody;
    }
}
