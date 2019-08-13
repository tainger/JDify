package io.terminus.dalaran.console.model.dto;

import io.terminus.dalaran.console.model.dto.basic.BasicClientInfo;
import lombok.Data;

@Data
public class ClientDTO extends BasicClientInfo {

    private String clientId;

    private String secret;

    private String description;
}
