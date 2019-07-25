package io.terminus.dalaran.core.component.model;

import lombok.Data;

@Data
public class ServiceOperation {

    private String operationKey;

    private Long inModelId;

    private Long outModelId;
}
