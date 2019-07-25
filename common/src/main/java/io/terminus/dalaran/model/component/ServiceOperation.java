package io.terminus.dalaran.model.component;

import lombok.Data;

@Data
public class ServiceOperation {

    private String operationKey;

    private Long inModelId;

    private Long outModelId;
}
