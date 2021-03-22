package io.terminus.dalaran.model;

import lombok.Data;

@Data
public class BasicResponse {

    private Boolean success;

    private Object result;

    public BasicResponse(Boolean success) {
        this.success = success;
    }

    public BasicResponse(Boolean success, Object result) {
        this.success = success;
        this.result = result;
    }
}
