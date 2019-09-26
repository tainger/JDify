package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.OnExceptionMessage;
import io.terminus.dalaran.console.exception.OnExceptionNotThrows;
import io.terminus.dalaran.console.repository.PropertyRepository;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.exception.flow.CreateFlowException;
import io.terminus.dalaran.exception.flow.FlowNotExistException;
import io.terminus.dalaran.exception.flow.UpdateFlowException;
import io.terminus.dalaran.model.dto.*;
import io.terminus.dalaran.model.dto.flow.ImportFlowDTO;
import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.model.dto.log.MainLogDTO;
import io.terminus.dalaran.model.flow.FlowValidation;
import io.terminus.dalaran.model.query.FlowQuery;
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
    @OnExceptionMessage(value = ResponseMessage.FLOW_CREATE_ERROR)
    public Long create(@RequestBody TriggerFlowDTO model) throws CreateFlowException {
        try {
            return flowManagementService.createFlow(model);
        } catch (Exception e) {
            throw new CreateFlowException(e.getMessage());
        }
    }

    @Override
    @OnExceptionMessage(value = ResponseMessage.FLOW_UPDATE_ERROR)
    public TriggerFlowDTO update(@RequestBody TriggerFlowDTO model) throws UpdateFlowException, FlowNotExistException {
        try {
            return flowManagementService.updateFlow(model);
        } catch (FlowNotExistException e) {
            throw e;
        } catch (Exception e) {
            throw new UpdateFlowException(e.getMessage());
        }
    }

    @Override
    @OnExceptionMessage(value = ResponseMessage.FLOW_CREATE_ERROR)
    public ImportFlowResult importTriggerFlow(@RequestBody ImportFlowDTO model) {
        return flowManagementService.importFlow(model);
    }

    @Override
    @OnExceptionMessage(value = ResponseMessage.FLOW_CREATE_ERROR)
    public ImportProcessorResult importProcessor(@RequestBody ImportProcessorDTO model) {
        return flowManagementService.importProcessor(model);
    }

    @Override
    @OnExceptionMessage(value = ResponseMessage.FLOW_DELETE_ERROR)
    public void deleteById(@RequestParam Long id) {
        flowManagementService.deleteFlow(id);
    }

    @Override
    @OnExceptionNotThrows(CreateFlowException.class)
    @OnExceptionMessage(value = ResponseMessage.FLOW_COPY_ERROR)
    public Long copy(@RequestBody CopyFlow copyFlow) throws FlowNotExistException {
        return flowManagementService.copyFlow(copyFlow);
    }

    @Override
    @OnExceptionMessage(value = ResponseMessage.FLOW_QUERY_ERROR)
    public TriggerFlowDTO getById(@PathVariable Long id) {
        return flowManagementService.getById(id);
    }

    @Override
    @OnExceptionMessage(value = ResponseMessage.FLOW_QUERY_ERROR)
    public List<TriggerFlowDTO> query(FlowQuery query) {
        return flowManagementService.queryFlows(query);
    }

    @Override
    @OnExceptionMessage(value = ResponseMessage.FLOW_QUERY_ERROR)
    public List<TriggerFlowDTO> list() {
        return flowManagementService.list();
    }

    @Override
    @OnExceptionMessage(value = ResponseMessage.FLOW_CHECK_ERROR)
    public List<FlowValidation> validate(@RequestBody TriggerFlowDTO model) {
        return flowManagementService.validateFlow(model);
    }

    @Override
    @OnExceptionMessage(value = ResponseMessage.FLOW_TEST_ERROR)
    public MainLogDTO doTest(@RequestBody TestRequestDTO request) {
        TriggerFlowDTO flow = flowManagementService.getById(request.getFlowId());
        if (flow == null) {
            // TODO throw flow not found
            return null;
        }
        String recordId = dalaranContext.testFlow(request.getFlowId(), request.getBody());
        return tracingLogService.getRecordDetail(recordId);
    }
}
