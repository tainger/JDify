package io.terminus.dalaran.component.processor.sgm.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SGMSignInfo implements Serializable {

    private String accessToken;

    private String timestamp;

    public SGMSignInfo() {
    }

    public SGMSignInfo(String accessToken, String timestamp) {
        this.accessToken = accessToken;
        this.timestamp = timestamp;
    }
}
