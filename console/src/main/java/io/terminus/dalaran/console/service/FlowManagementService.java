package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.exception.flow.FlowNotExistException;
import io.terminus.dalaran.model.dto.CopyFlow;
import io.terminus.dalaran.model.dto.ImportFlowResult;
import io.terminus.dalaran.model.dto.ImportProcessorDTO;
import io.terminus.dalaran.model.dto.ImportProcessorResult;
import io.terminus.dalaran.model.dto.basic.BasicFlowInfo;
import io.terminus.dalaran.model.dto.flow.ImportFlowDTO;
import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.model.flow.FlowValidation;
import io.terminus.dalaran.model.query.FlowQuery;
import io.terminus.dalaran.response.ResponseResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface FlowManagementService {

    Long saveFlow(TriggerFlowEntity flowEntity);

    Long createFlow(TriggerFlowDTO flowModel);

    ImportFlowResult importFlow(ImportFlowDTO flowModel);

    ImportProcessorResult importProcessor(ImportProcessorDTO model);

    void deleteFlow(Long flowId);

    TriggerFlowDTO updateFlow(TriggerFlowDTO flowModel) throws FlowNotExistException;

    List<TriggerFlowDTO> queryFlows(FlowQuery query);

    List<TriggerFlowDTO> list();

    List<TriggerFlowDTO> queryByProcessorIds(List<Long> processorIds);

    List<BasicFlowInfo> listBasicFlowInfoByModuleId(Long moduleId);

    @Nullable
    TriggerFlowDTO getById(Long flowId);

    @Nullable
    TriggerFlowDTO getByIdVersion(Long flowId,String version) throws FlowNotExistException;

    Long copyFlow(CopyFlow copyFlow) throws FlowNotExistException;

    List<FlowValidation> validateFlow(TriggerFlowDTO model);

    List<String> listTriggerOperations();

    ResponseResult offline(TriggerFlowDTO flowDTO);

    ResponseResult online(TriggerFlowDTO flowDTO);

}
