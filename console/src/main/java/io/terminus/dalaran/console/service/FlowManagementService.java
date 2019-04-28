package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.model.query.rst.ModuleComponent;
import io.terminus.dalaran.entity.flow.TriggerFlowEntity;
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

    List<ModuleComponent> getComponents(Long moduleId);

    @Nullable
    TriggerFlowDTO getById(Long flowId);
}
