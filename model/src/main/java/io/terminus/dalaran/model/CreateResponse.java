package io.terminus.dalaran.model;

import lombok.Data;

@Data
public class CreateResponse {

    private String id;

    public CreateResponse(String id) {
        this.id = id;
    }
}
