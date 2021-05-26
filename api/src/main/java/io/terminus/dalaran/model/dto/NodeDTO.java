package io.terminus.dalaran.model.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class NodeDTO{

    private String resourceKey;

    private String name;

    private String company;

    private String application;

    private String system;

}
