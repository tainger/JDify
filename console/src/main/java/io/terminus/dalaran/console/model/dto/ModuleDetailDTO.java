package io.terminus.dalaran.console.model.dto;

import io.terminus.dalaran.console.model.dto.flow.BasicFlowInfo;
import lombok.Data;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@Data
public class ModuleDetailDTO extends ModuleDTO {

    private List<BasicFlowInfo> flows;

    private List<BasicModelInfo> models;

    private List<BasicConnectorInfo> connectors;

    private List<BasicServiceInfo> services;

}
