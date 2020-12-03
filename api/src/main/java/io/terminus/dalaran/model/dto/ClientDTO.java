package io.terminus.dalaran.model.dto;

import io.terminus.dalaran.model.dto.basic.BasicClientInfo;
import lombok.Data;

@Data
public class ClientDTO extends BasicClientInfo {

    private String appKey;

    private String secret;

    private String description;

    private boolean isExist;
}
