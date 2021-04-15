package io.terminus.dalaran.model.dto;

import io.terminus.dalaran.model.dto.flow.SubFlowDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PrivateRelationResource {

    private List<SubFlowDTO> subFlows = new ArrayList<>();

    private List<ModelDTO> models = new ArrayList<>();

    private List<ConnectorDTO> connectors = new ArrayList<>();

    private List<ServiceDTO> services = new ArrayList<>();

    private List<FunctionDTO> functions = new ArrayList<>();
}
