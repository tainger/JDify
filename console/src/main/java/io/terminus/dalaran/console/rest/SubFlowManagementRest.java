package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.console.model.dto.flow.SubFlowDTO;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.service.SubFlowManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sub-flow")
public class SubFlowManagementRest {

    @Autowired
    private SubFlowManagementService service;

    @ApiOperation(value = "创建子流程")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Long create(@RequestBody SubFlowDTO model) {
        return service.createFlow(model);
    }

    @ApiOperation(value = "更新子流程")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public SubFlowDTO update(@RequestBody SubFlowDTO model) {
        return service.updateFlow(model);
    }

    @ApiOperation(value = "删除子流程")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void delete(@RequestParam Long id) {
        service.deleteFlow(id);
    }

    @ApiOperation(value = "复制子流程")
    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    public Long copy(@RequestParam Long id) {
        return service.copyFlow(id);
    }

    @ApiOperation(value = "条件查询子流程")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public List<SubFlowDTO> query(FlowQuery query) {
        return service.queryFlows(query);
    }

    @ApiOperation(value = "全量查询子流程")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<SubFlowDTO> list() {
        return service.list();
    }
}
