package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.dto.CopyFlow;
import io.terminus.dalaran.console.model.dto.basic.BasicFlowInfo;
import io.terminus.dalaran.console.model.dto.flow.SubFlowDTO;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.model.flow.FlowValidation;
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

    Long copyFlow(CopyFlow copyForm);

    List<FlowValidation> validateFlow(SubFlowDTO model);

    List<BasicFlowInfo> listBasicSubFlowInfoByModuleId(Long moduleId);
}
