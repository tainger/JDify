package io.terminus.dalaran.console.model.dto.trantor;

import lombok.Data;

import java.util.List;

@Data
public class TrantorModuleDTO {
    private String key;

    private String name;

    private List<IntegrationInfoDTO> integrations;
}
