package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.DalaranExceptionBuilder;
import io.terminus.dalaran.console.exception.OnException;
import io.terminus.dalaran.console.repository.PropertyRepository;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.exception.flow.CreateFlowException;
import io.terminus.dalaran.exception.flow.FlowNotExistException;
import io.terminus.dalaran.exception.flow.UpdateFlowException;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.CreateResponse;
import io.terminus.dalaran.model.dto.*;
import io.terminus.dalaran.model.dto.flow.BindAlarmRuleDto;
import io.terminus.dalaran.model.dto.flow.ImportFlowDTO;
import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.model.dto.log.MainLogDTO;
import io.terminus.dalaran.model.flow.FlowValidation;
import io.terminus.dalaran.model.query.FlowQuery;
import io.terminus.dalaran.response.ResponseResult;
import io.terminus.dalaran.rest.read.FlowReadAPI;
import io.terminus.dalaran.rest.write.FlowWriteAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FlowManagementRest implements FlowReadAPI, FlowWriteAPI {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private FlowManagementService flowManagementService;

    @Autowired
    private ModelManagementService modelManagementService;

    @Autowired
    private TracingLogService tracingLogService;

    @Autowired
    private DalaranContext dalaranContext;

    @Override
    @OnException(code = ResponseMessage.FLOW_CREATE_ERROR)
    public CreateResponse create(@RequestBody TriggerFlowDTO model) throws CreateFlowException {
        try {
            return new CreateResponse(flowManagementService.createFlow(model));
        } catch (Exception e) {
            throw DalaranExceptionBuilder.build(CreateFlowException.class, e.getMessage());
        }
    }

    @Override
    public BasicResponse create(BasicResourceRequest template) throws CreateFlowException {
        return flowManagementService.createFromTemplate(template);
    }

    @Override
    @OnException(code = ResponseMessage.FLOW_UPDATE_ERROR)
    public TriggerFlowDTO update(@RequestBody TriggerFlowDTO model) throws UpdateFlowException, FlowNotExistException {
        try {
            return flowManagementService.updateFlow(model);
        } catch (FlowNotExistException e) {
            throw e;
        } catch (Exception e) {
            throw DalaranExceptionBuilder.build(UpdateFlowException.class, e.getMessage());
        }
    }

    @Override
    @OnException(code = ResponseMessage.FLOW_CREATE_ERROR)
    public ImportFlowResult importTriggerFlow(@RequestBody ImportFlowDTO model) {
        return flowManagementService.importFlow(model);
    }

    @Override
    @OnException(code = ResponseMessage.FLOW_CREATE_ERROR)
    public ImportProcessorResult importProcessor(@RequestBody ImportProcessorDTO model) {
        return flowManagementService.importProcessor(model);
    }

    @Override
    public BasicResponse checkTemplateVersion(BasicResourceRequest flow) throws CreateFlowException {
        return flowManagementService.checkTemplateVersion(flow);
    }

    @Override
    public ResponseResult offline(@RequestBody TriggerFlowDTO flowDTO) {
        return flowManagementService.offline(flowDTO);
    }

    @Override
    public ResponseResult online(@RequestBody TriggerFlowDTO flowDTO) {
        return flowManagementService.online(flowDTO);
    }

    @Override
    @OnException(code = ResponseMessage.FLOW_TRIGGER_ERROR)
    public void trigger(@RequestBody TriggerFlowDTO flowDTO) {
        dalaranContext.trigger(flowDTO.getId());
    }

    @Override
    public ResponseResult bindAlarm(BindAlarmRuleDto bindAlarmRuleDto) {
        return flowManagementService.bindAlarm(bindAlarmRuleDto);
    }

    @Override
    @OnException(code = ResponseMessage.FLOW_DELETE_ERROR)
    public void deleteById(@RequestParam String id) {
        flowManagementService.deleteFlow(id);
    }

    @Override
    @OnException(code = ResponseMessage.FLOW_COPY_ERROR, exception = CreateFlowException.class)
    public CreateResponse copy(@RequestBody CopyFlow copyFlow) throws FlowNotExistException {
        return new CreateResponse(flowManagementService.copyFlow(copyFlow));
    }

    @Override
    @OnException(code = ResponseMessage.FLOW_QUERY_ERROR)
    public TriggerFlowDTO getById(@PathVariable String id) {
        return flowManagementService.getById(id);
    }

    @Override
    @OnException(code = ResponseMessage.FLOW_QUERY_ERROR)
    public TriggerFlowDTO getByIdVersion(String id, String version) throws FlowNotExistException{
        return flowManagementService.getByIdVersion(id, version);
    }

    @Override
    @OnException(code = ResponseMessage.FLOW_QUERY_ERROR)
    public List<TriggerFlowDTO> query(FlowQuery query) {
        return flowManagementService.queryFlows(query);
    }

    @Override
    @OnException(code = ResponseMessage.FLOW_QUERY_ERROR)
    public List<TriggerFlowDTO> list() {
        return flowManagementService.list();
    }

    @Override
    @OnException(code = ResponseMessage.FLOW_CHECK_ERROR)
    public List<FlowValidation> validate(@RequestBody TriggerFlowDTO model) {
        return flowManagementService.validateFlow(model);
    }

    @Override
    @OnException(code = ResponseMessage.FLOW_TEST_ERROR)
    public MainLogDTO doTest(@RequestBody TestRequestDTO request) {
        TriggerFlowDTO flow = flowManagementService.getById(request.getFlowId());
        if (flow == null) {
            // TODO throw flow not found
            return null;
        }
        String recordId = dalaranContext.testFlow(request.getFlowId(), request.getBody());
        return tracingLogService.getRecordDetail(recordId);
    }

    @Override
    public BasicResponse saveAsTemplate(BasicResourceRequest flow) {
        return flowManagementService.saveAsTemplate(flow);
    }
}
