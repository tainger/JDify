package io.terminus.dalaran.console.model.dto.trantor;


import lombok.Data;

@Data
public class IntegrationPointDTO {
    private String key;

    private String name;

    private Long inModelId;

    private Long outModelId;
}
