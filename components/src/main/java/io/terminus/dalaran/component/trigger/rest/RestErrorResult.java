package io.terminus.dalaran.component.trigger.rest;

import lombok.Data;

@Data
public class RestErrorResult {

    private int code;

    private String message;

    public RestErrorResult(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
