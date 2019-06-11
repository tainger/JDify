package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.dto.flow.BasicFlowInfo;
import io.terminus.dalaran.console.model.dto.flow.SubFlowDTO;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.core.flow.model.FlowValidation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface SubFlowManagementService {


    Long createFlow(SubFlowDTO flowModel);

    void deleteFlow(Long flowId);

    SubFlowDTO updateFlow(SubFlowDTO flowModel);

    List<SubFlowDTO> queryFlows(FlowQuery query);

    List<SubFlowDTO> list();

    @Nullable
    SubFlowDTO getById(Long flowId);

    Long copyFlow(Long id);

    List<FlowValidation> validateFlow(SubFlowDTO model);

    List<BasicFlowInfo> listBasicSubFlowInfoByModuleId(Long moduleId);
}
