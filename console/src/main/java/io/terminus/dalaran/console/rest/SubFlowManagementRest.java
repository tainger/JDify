package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.common.model.Response;
import io.terminus.dalaran.console.model.ResponseMessage;
import io.terminus.dalaran.console.model.TestRequestDTO;
import io.terminus.dalaran.console.model.dto.CopyFlow;
import io.terminus.dalaran.console.model.dto.flow.SubFlowDTO;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.service.SubFlowManagementService;
import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.core.context.DalaranContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sub-flow")
public class SubFlowManagementRest {

    @Autowired
    private SubFlowManagementService service;

    @Autowired
    private TracingLogService tracingLogService;

    @Autowired
    private DalaranContext dalaranContext;

    @ApiOperation(value = "创建子流程")
    @PostMapping(value = "/create")
    public Response create(@RequestBody SubFlowDTO model) {
        try {
            return Response.ok(service.createFlow(model));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.SUBFLOW_CREATE_ERROR);
        }
    }

    @ApiOperation(value = "更新子流程")
    @PostMapping(value = "/update")
    public Response update(@RequestBody SubFlowDTO model) {
        try {
            return Response.ok(service.updateFlow(model));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.SUBFLOW_UPDATE_ERROR);
        }
    }

    @ApiOperation(value = "删除子流程")
    @DeleteMapping(value = "/delete")
    public Response delete(@RequestParam Long id) {
        try {
            service.deleteFlow(id);
            return Response.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.SUBFLOW_DELETE_ERROR);
        }
    }

    @ApiOperation(value = "复制子流程")
    @PostMapping(value = "/copy")
    public Response copy(@RequestBody CopyFlow copyFlow) {
        try {
            return Response.ok(service.copyFlow(copyFlow));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.SUBFLOW_COPY_ERROR);
        }
    }

    @ApiOperation(value = "根据 ID 获取子流程")
    @GetMapping(value = "/{id}")
    public Response getById(@PathVariable Long id) {
        try {
            return Response.ok(service.getById(id));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.SUBFLOW_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "条件查询子流程")
    @GetMapping(value = "/query")
    public Response query(FlowQuery query) {
        try {
            return Response.ok(service.queryFlows(query));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.SUBFLOW_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "全量查询子流程")
    @GetMapping(value = "/list")
    public Response list() {
        try {
            return Response.ok(service.list());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.SUBFLOW_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "检查子流程")
    @PostMapping(value = "/validate")
    public Response validate(@RequestBody SubFlowDTO model) {
        try {
            return Response.ok(service.validateFlow(model));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.SUBFLOW_CHECK_ERROR);
        }
    }

    @ApiOperation(value = "测试子流程")
    @PostMapping("/test")
    private Response doSubFlowTest(@RequestBody TestRequestDTO request) {
        try {
            SubFlowDTO flow = service.getById(request.getFlowId());
            if (flow == null) {
                // TODO throw flow not found
                return null;
            }
            String recordId = dalaranContext.testSubFlow(request.getFlowId(), request.getBody());
            return Response.ok(tracingLogService.getRecordDetail(recordId));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.SUBFLOW_TEST_ERROR);
        }
    }
}
