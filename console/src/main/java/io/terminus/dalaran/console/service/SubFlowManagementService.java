package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.CopyFlow;
import io.terminus.dalaran.model.dto.basic.BasicFlowInfo;
import io.terminus.dalaran.model.dto.flow.SubFlowDTO;
import io.terminus.dalaran.model.flow.FlowValidation;
import io.terminus.dalaran.model.query.FlowQuery;
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
