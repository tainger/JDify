package io.terminus.dalaran.api.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.CopyFlow;
import io.terminus.dalaran.model.dto.TestRequestDTO;
import io.terminus.dalaran.model.dto.flow.SubFlowDTO;
import io.terminus.dalaran.model.dto.log.MainLogDTO;
import io.terminus.dalaran.model.flow.FlowValidation;
import io.terminus.dalaran.model.query.FlowQuery;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "/api/sub-flow", produces = {"application/json; charset=UTF-8"})
public interface SubFlowRestAPI {

    @ApiOperation(value = "创建子流程")
    @PostMapping(value = "/create")
    Long create(@RequestBody SubFlowDTO model);

    @ApiOperation(value = "更新子流程")
    @PostMapping(value = "/update")
    SubFlowDTO update(@RequestBody SubFlowDTO model);

    @ApiOperation(value = "删除子流程")
    @DeleteMapping(value = "/delete")
    void delete(@RequestParam Long id);

    @ApiOperation(value = "复制子流程")
    @PostMapping(value = "/copy")
    Long copy(@RequestBody CopyFlow copyFlow);

    @ApiOperation(value = "根据 ID 获取子流程")
    @GetMapping(value = "/{id}")
    SubFlowDTO getById(@PathVariable Long id);

    @ApiOperation(value = "条件查询子流程")
    @GetMapping(value = "/query")
    List<SubFlowDTO> query(FlowQuery query);

    @ApiOperation(value = "全量查询子流程")
    @GetMapping(value = "/list")
    List<SubFlowDTO> list();

    @ApiOperation(value = "检查子流程")
    @PostMapping(value = "/validate")
    List<FlowValidation> validate(@RequestBody SubFlowDTO model);

    @ApiOperation(value = "测试子流程")
    @PostMapping("/test")
    MainLogDTO doSubFlowTest(@RequestBody TestRequestDTO request);
}
