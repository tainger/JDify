package io.terminus.dalaran.model.component;

import lombok.Data;

@Data
public class ServiceOperation {

    private String operationKey;

    private String inModelId;

    private String outModelId;
}
