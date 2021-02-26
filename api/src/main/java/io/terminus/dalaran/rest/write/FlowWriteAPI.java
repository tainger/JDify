package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.exception.flow.CreateFlowException;
import io.terminus.dalaran.exception.flow.FlowNotExistException;
import io.terminus.dalaran.exception.flow.UpdateFlowException;
import io.terminus.dalaran.model.dto.CopyFlow;
import io.terminus.dalaran.model.dto.ImportFlowResult;
import io.terminus.dalaran.model.dto.ImportProcessorDTO;
import io.terminus.dalaran.model.dto.ImportProcessorResult;
import io.terminus.dalaran.model.dto.flow.ImportFlowDTO;
import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/flow", produces = {"application/json; charset=UTF-8"})
public interface FlowWriteAPI {

    @ApiOperation(value = "创建集成流")
    @PostMapping(value = "/create")
    Long create(@RequestBody TriggerFlowDTO model) throws CreateFlowException;

    @ApiOperation(value = "更新集成流")
    @PostMapping(value = "/update")
    TriggerFlowDTO update(@RequestBody TriggerFlowDTO model) throws FlowNotExistException, UpdateFlowException;

    @ApiOperation(value = "删除集成流")
    @DeleteMapping(value = "/delete")
    void deleteById(@RequestParam Long id);

    @ApiOperation(value = "复制集成流")
    @PostMapping(value = "/copy")
    Long copy(@RequestBody CopyFlow copyFlow) throws FlowNotExistException, CreateFlowException;

    @ApiOperation(value = "快速创建集成流")
    @PostMapping(value = "/import")
    ImportFlowResult importTriggerFlow(@RequestBody ImportFlowDTO model);

    @ApiOperation(value = "快速创建处理器")
    @PostMapping(value = "/importProcessor")
    ImportProcessorResult importProcessor(@RequestBody ImportProcessorDTO model);

    @ApiOperation(value = "下线该流程")
    @PostMapping(value = "/offline")
    ResponseResult offline(@RequestBody TriggerFlowDTO flowDTO);

    @ApiOperation(value = "上线该流程")
    @PostMapping(value = "/online")
    ResponseResult online(@RequestBody TriggerFlowDTO flowDTO);
}
