package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.model.dto.CopyFlow;
import io.terminus.dalaran.model.dto.ImportFlowResult;
import io.terminus.dalaran.model.dto.ImportProcessorDTO;
import io.terminus.dalaran.model.dto.ImportProcessorResult;
import io.terminus.dalaran.model.dto.basic.BasicFlowInfo;
import io.terminus.dalaran.model.dto.flow.ImportFlowDTO;
import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.model.flow.FlowValidation;
import io.terminus.dalaran.model.query.FlowQuery;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface FlowManagementService {

    Long saveFlow(TriggerFlowEntity flowEntity);

    Long createFlow(TriggerFlowDTO flowModel);

    ImportFlowResult importFlow(ImportFlowDTO flowModel);

    ImportProcessorResult importProcessor(ImportProcessorDTO model);

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
