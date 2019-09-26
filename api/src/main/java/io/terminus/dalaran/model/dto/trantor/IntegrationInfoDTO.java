package io.terminus.dalaran.model.dto.trantor;

import lombok.Data;

import java.util.List;

@Data
public class IntegrationInfoDTO {
    private String key;

    private String name;

    private List<IntegrationPointDTO> integrationPoints;
}
