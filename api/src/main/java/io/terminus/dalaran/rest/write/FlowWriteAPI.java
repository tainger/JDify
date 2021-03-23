package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.exception.flow.CreateFlowException;
import io.terminus.dalaran.exception.flow.FlowNotExistException;
import io.terminus.dalaran.exception.flow.UpdateFlowException;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.CreateResponse;
import io.terminus.dalaran.model.dto.*;
import io.terminus.dalaran.model.dto.flow.BindAlarmRuleDto;
import io.terminus.dalaran.model.dto.flow.ImportFlowDTO;
import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/flow", produces = {"application/json; charset=UTF-8"})
public interface FlowWriteAPI {

    @ApiOperation(value = "创建集成流")
    @PostMapping(value = "/create")
    CreateResponse create(@RequestBody TriggerFlowDTO flow) throws CreateFlowException;

    @ApiOperation(value = "基于模板创建集成流")
    @PostMapping(value = "/create/template")
    BasicResponse create(@RequestBody TemplatePrecipitationDTO template) throws CreateFlowException;

    @ApiOperation(value = "更新集成流")
    @PostMapping(value = "/update")
    TriggerFlowDTO update(@RequestBody TriggerFlowDTO flow) throws FlowNotExistException, UpdateFlowException;

    @ApiOperation(value = "保存集成流为模板")
    @PostMapping(value = "/save/template")
    BasicResponse saveAsTemplate(@RequestBody TemplatePrecipitationDTO flow) throws CreateFlowException;

    @ApiOperation(value = "删除集成流")
    @DeleteMapping(value = "/delete")
    void deleteById(@RequestParam String id);

    @ApiOperation(value = "复制集成流")
    @PostMapping(value = "/copy")
    CreateResponse copy(@RequestBody CopyFlow copyFlow) throws FlowNotExistException, CreateFlowException;

    @ApiOperation(value = "快速创建集成流")
    @PostMapping(value = "/import")
    ImportFlowResult importTriggerFlow(@RequestBody ImportFlowDTO flow);

    @ApiOperation(value = "快速创建处理器")
    @PostMapping(value = "/importProcessor")
    ImportProcessorResult importProcessor(@RequestBody ImportProcessorDTO flow);

    @ApiOperation(value = "流程下线")
    @PostMapping(value = "/offline")
    ResponseResult offline(@RequestBody TriggerFlowDTO flowDTO);

    @ApiOperation(value = "流程上线")
    @PostMapping(value = "/online")
    ResponseResult online(@RequestBody TriggerFlowDTO flowDTO);

    @ApiOperation(value = "流程触发")
    @PostMapping(value = "/trigger")
    void trigger(@RequestBody TriggerFlowDTO flowDTO);

    @ApiOperation(value = "流程绑定报警")
    @PostMapping(value = "/trigger/bind")
    ResponseResult bindAlarm(@RequestBody BindAlarmRuleDto bindAlarmRuleDto);
}
