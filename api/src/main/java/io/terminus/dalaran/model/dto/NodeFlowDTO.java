package io.terminus.dalaran.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class NodeFlowDTO extends NodeDTO {

    private Map<String, Object> config;

}
