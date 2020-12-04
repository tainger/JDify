package io.terminus.dalaran.component.as2.processor.model;

import lombok.Data;

@Data
public class EDIRequestResult {

    private String status;

    private Boolean success = true;

    private String requestMessageId;

    private String responseMessageId;

    private String message;
}
