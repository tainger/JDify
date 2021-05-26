package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.exception.flow.CreateFlowException;
import io.terminus.dalaran.exception.flow.FlowNotExistException;
import io.terminus.dalaran.exception.flow.FlowTestException;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.dto.*;
import io.terminus.dalaran.model.dto.flow.BasicFlowInfoDTO;
import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.model.dto.log.MainLogDTO;
import io.terminus.dalaran.model.flow.FlowValidation;
import io.terminus.dalaran.model.query.FlowQuery;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequestMapping(value = "/api/flow", produces = {"application/json; charset=UTF-8"})
public interface FlowReadAPI {

    @ApiOperation(value = "根据 ID 获取集成流")
    @GetMapping(value = "/{id}")
    TriggerFlowDTO getById(@PathVariable String id);

    @ApiOperation(value = "根据 ID、版本 获取集成流")
    @GetMapping(value = "/byIdVersion")
    TriggerFlowDTO getByIdVersion(String id, String version) throws FlowNotExistException;

    @ApiOperation(value = "条件查询集成流")
    @GetMapping(value = "/query")
    List<TriggerFlowDTO> query(FlowQuery query);

    @ApiOperation(value = "全量查询集成流")
    @GetMapping(value = "/list")
    List<TriggerFlowDTO> list();

    @ApiOperation(value = "检查集成流")
    @PostMapping(value = "/validate")
    List<FlowValidation> validate(@RequestBody TriggerFlowDTO model);

    @ApiOperation(value = "测试集成流")
    @PostMapping("/test")
    MainLogDTO doTest(@RequestBody TestRequestDTO request) throws FlowTestException;

    @ApiOperation(value = "模板版本重复性校验")
    @PostMapping(value = "/check/template/version")
    BasicResponse checkTemplateVersion(@RequestBody BasicResourceRequest flow) throws CreateFlowException;

    @ApiOperation(value = "全量查询集成流(basic Info)")
    @GetMapping(value = "/pageable/basic")
    Page<BasicFlowInfoDTO> queryBasicInfo(FlowQuery flowQuery, @RequestParam Integer pageNumber, @RequestParam Integer pageSize);

    @ApiOperation(value = "生成node结构")
    @GetMapping(value = "/node")
    List<NodeDTO> node(@RequestBody List<ProcessorDTO> pipeline);
}
