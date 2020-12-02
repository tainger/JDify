package io.terminus.dalaran.component.http.trigger.model;

import lombok.Data;

// TODO 暂时不用统一返回
@Data
public class RestErrorResult {

    private int code;

    private String message;

    public RestErrorResult(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
