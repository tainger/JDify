package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.model.dto.CopyFlow;
import io.terminus.dalaran.console.model.dto.flow.BasicFlowInfo;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.core.flow.model.FlowValidation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface FlowManagementService {

    Long saveFlow(TriggerFlowEntity flowEntity);

    Long createFlow(TriggerFlowDTO flowModel);

    void deleteFlow(Long flowId);

    TriggerFlowDTO updateFlow(TriggerFlowDTO flowModel);

    List<TriggerFlowDTO> queryFlows(FlowQuery query);

    List<TriggerFlowDTO> list();

    List<TriggerFlowDTO> queryByProcessorIds(List<Long> processorIds);

    List<BasicFlowInfo> listBasicFlowInfoByModuleId(Long moduleId);

    @Nullable
    TriggerFlowDTO getById(Long flowId);

    Long copyFlow(CopyFlow copyFlow);

    List<FlowValidation> validateFlow(TriggerFlowDTO model);
}
