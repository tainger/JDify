package io.terminus.dalaran.component.processor.as2.model;

import lombok.Data;

@Data
public class EDIRequestResult {

    private String status;

    private Boolean success = true;

    private String requestMessageId;

    private String responseMessageId;

    private String message;
}
