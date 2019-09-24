package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.api.rest.FlowRestAPI;
import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.DalaranException;
import io.terminus.dalaran.console.repository.PropertyRepository;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.model.dto.*;
import io.terminus.dalaran.model.dto.flow.ImportFlowDTO;
import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.model.dto.log.MainLogDTO;
import io.terminus.dalaran.model.flow.FlowValidation;
import io.terminus.dalaran.model.query.FlowQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FlowManagementRest implements FlowRestAPI {

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
    @DalaranException(value = ResponseMessage.FLOW_CREATE_ERROR)
    public Long create(@RequestBody TriggerFlowDTO model) {
        return flowManagementService.createFlow(model);
    }

    @Override
    @DalaranException(value = ResponseMessage.FLOW_UPDATE_ERROR)
    public TriggerFlowDTO update(@RequestBody TriggerFlowDTO model) {
        return flowManagementService.updateFlow(model);
    }

    @Override
    @DalaranException(value = ResponseMessage.FLOW_CREATE_ERROR)
    public ImportFlowResult importTriggerFlow(@RequestBody ImportFlowDTO model) {
        return flowManagementService.importFlow(model);
    }

    @Override
    @DalaranException(value = ResponseMessage.FLOW_CREATE_ERROR)
    public ImportProcessorResult importProcessor(@RequestBody ImportProcessorDTO model) {
        return flowManagementService.importProcessor(model);
    }

    @Override
    @DalaranException(value = ResponseMessage.FLOW_DELETE_ERROR)
    public void delete(@RequestParam Long id) {
        flowManagementService.deleteFlow(id);
    }

    @Override
    @DalaranException(value = ResponseMessage.FLOW_COPY_ERROR)
    public Long copy(@RequestBody CopyFlow copyFlow) {
        return flowManagementService.copyFlow(copyFlow);
    }

    @Override
    @DalaranException(value = ResponseMessage.FLOW_QUERY_ERROR)
    public TriggerFlowDTO getById(@PathVariable Long id) {
        return flowManagementService.getById(id);
    }

    @Override
    @DalaranException(value = ResponseMessage.FLOW_QUERY_ERROR)
    public List<TriggerFlowDTO> query(FlowQuery query) {
        return flowManagementService.queryFlows(query);
    }

    @Override
    @DalaranException(value = ResponseMessage.FLOW_QUERY_ERROR)
    public List<TriggerFlowDTO> list() {
        return flowManagementService.list();
    }

    @Override
    @DalaranException(value = ResponseMessage.FLOW_CHECK_ERROR)
    public List<FlowValidation> validate(@RequestBody TriggerFlowDTO model) {
        return flowManagementService.validateFlow(model);
    }

    @Override
    @DalaranException(value = ResponseMessage.FLOW_TEST_ERROR)
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
