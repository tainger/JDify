package io.terminus.dalaran.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class BasicResponse implements Serializable {

    private Boolean success;

    private Object result;

    public BasicResponse() {
    }

    public BasicResponse(Boolean success) {
        this.success = success;
    }

    public BasicResponse(Boolean success, Object result) {
        this.success = success;
        this.result = result;
    }
}
