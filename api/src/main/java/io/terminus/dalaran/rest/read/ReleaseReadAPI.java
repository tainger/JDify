package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.ReleaseRecordDTO;
import io.terminus.dalaran.model.dto.flow.ReleaseFlowDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping(value = "/api/platform", produces = {"application/json; charset=UTF-8"})
public interface ReleaseReadAPI {

    @ApiOperation(value = "获取某版本所有流新消息")
    @GetMapping(value = "/release")
    List<ReleaseRecordDTO> releaseRecordList();

    @ApiOperation(value = "获取某版本所有流新消息")
    @GetMapping(value = "/release/{version:.*}")
    List<ReleaseFlowDTO> triggerFlowList(@PathVariable String version);

    @ApiOperation(value = "流程统计分页")
    @GetMapping(value = "/release/list")
    Page<ReleaseFlowDTO> triggerFlowListByPage(@RequestParam Integer pageNumber, @RequestParam Integer pageSize);

}
