package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.exception.flow.FlowNotExistException;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.dto.*;
import io.terminus.dalaran.model.dto.basic.BasicFlowInfo;
import io.terminus.dalaran.model.dto.flow.BasicFlowInfoDTO;
import io.terminus.dalaran.model.dto.flow.BindAlarmRuleDTO;
import io.terminus.dalaran.model.dto.flow.ImportFlowDTO;
import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.model.flow.FlowValidation;
import io.terminus.dalaran.model.query.FlowQuery;
import io.terminus.dalaran.response.ResponseResult;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface FlowManagementService {

    String saveFlow(TriggerFlowEntity flowEntity);

    String createFlow(TriggerFlowDTO flowModel);

    BasicResponse createFromTemplate(BasicResourceRequest template);

    BasicResponse saveAsTemplate(BasicResourceRequest flow);

    BasicResponse checkTemplateVersion(BasicResourceRequest flow);

    ImportFlowResult importFlow(ImportFlowDTO flowModel);

    ImportProcessorResult importProcessor(ImportProcessorDTO model);

    void deleteFlow(String flowId);

    TriggerFlowDTO updateFlow(TriggerFlowDTO flowModel) throws FlowNotExistException;

    List<TriggerFlowDTO> queryFlows(FlowQuery query);

    List<TriggerFlowDTO> list();

    List<TriggerFlowDTO> queryByProcessorIds(List<String> processorIds);

    List<BasicFlowInfo> listBasicFlowInfoByModuleId(String moduleId);

    @Nullable
    TriggerFlowDTO getById(String flowId);

    @Nullable
    TriggerFlowDTO getByIdVersion(String flowId, String version) throws FlowNotExistException;

    String copyFlow(CopyFlow copyFlow) throws FlowNotExistException;

    List<FlowValidation> validateFlow(TriggerFlowDTO model);

    List<String> listTriggerOperations();

    ResponseResult offline(TriggerFlowDTO flowDTO);

    ResponseResult online(TriggerFlowDTO flowDTO);

    ResponseResult bindAlarm(BindAlarmRuleDTO bindAlarmRuleDto);

    Page<BasicFlowInfoDTO> listBasicInfo(FlowQuery flowQuery, Integer pageNumber, Integer pageSize);

    List<NodeFlowListDTO> node(PipelineListDTO pipeline);
}
