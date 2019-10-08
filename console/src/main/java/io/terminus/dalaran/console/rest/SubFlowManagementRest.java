package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.OnException;
import io.terminus.dalaran.console.service.SubFlowManagementService;
import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.model.dto.CopyFlow;
import io.terminus.dalaran.model.dto.TestRequestDTO;
import io.terminus.dalaran.model.dto.flow.SubFlowDTO;
import io.terminus.dalaran.model.dto.log.MainLogDTO;
import io.terminus.dalaran.model.flow.FlowValidation;
import io.terminus.dalaran.model.query.FlowQuery;
import io.terminus.dalaran.rest.read.SubFlowReadAPI;
import io.terminus.dalaran.rest.write.SubFlowWriteAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SubFlowManagementRest implements SubFlowReadAPI, SubFlowWriteAPI {

    @Autowired
    private SubFlowManagementService service;

    @Autowired
    private TracingLogService tracingLogService;

    @Autowired
    private DalaranContext dalaranContext;

    @Override
    @OnException(message = ResponseMessage.SUBFLOW_CREATE_ERROR)
    public Long create(@RequestBody SubFlowDTO model) {
        return service.createFlow(model);
    }

    @Override
    @OnException(message = ResponseMessage.SUBFLOW_UPDATE_ERROR)
    public SubFlowDTO update(@RequestBody SubFlowDTO model) {
        return service.updateFlow(model);
    }

    @Override
    @OnException(message = ResponseMessage.SUBFLOW_DELETE_ERROR)
    public void deleteById(@RequestParam Long id) {
        service.deleteFlow(id);
    }

    @Override
    @OnException(message = ResponseMessage.SUBFLOW_COPY_ERROR)
    public Long copy(@RequestBody CopyFlow copyFlow) {
        return service.copyFlow(copyFlow);
    }

    @Override
    @OnException(message = ResponseMessage.SUBFLOW_QUERY_ERROR)
    public SubFlowDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Override
    @OnException(message = ResponseMessage.SUBFLOW_QUERY_ERROR)
    public List<SubFlowDTO> query(FlowQuery query) {
        return service.queryFlows(query);
    }


    @Override
    @OnException(message = ResponseMessage.SUBFLOW_QUERY_ERROR)
    public List<SubFlowDTO> list() {
        return service.list();
    }


    @Override
    @OnException(message = ResponseMessage.SUBFLOW_CHECK_ERROR)
    public List<FlowValidation> validate(@RequestBody SubFlowDTO model) {
        return service.validateFlow(model);
    }


    @Override
    @OnException(message = ResponseMessage.SUBFLOW_TEST_ERROR)
    public MainLogDTO doSubFlowTest(@RequestBody TestRequestDTO request) {
        SubFlowDTO flow = service.getById(request.getFlowId());
        if (flow == null) {
            // TODO throw flow not found
            return null;
        }
        String recordId = dalaranContext.testSubFlow(request.getFlowId(), request.getBody());
        return tracingLogService.getRecordDetail(recordId);
    }
}
