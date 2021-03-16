package io.terminus.dalaran.model;

import lombok.Data;

@Data
public class BasicResponse {

    private Boolean success;

    public BasicResponse(Boolean success) {
        this.success = success;
    }
}
