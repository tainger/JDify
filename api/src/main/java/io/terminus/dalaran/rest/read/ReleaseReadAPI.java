package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.ReleaseRecordDTO;
import io.terminus.dalaran.model.dto.flow.ReleaseFlowDTO;
import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping(value = "/api/platform", produces = {"application/json; charset=UTF-8"})
public interface ReleaseReadAPI {

    @ApiOperation(value = "获取某版本所有流新消息")
    @GetMapping(value = "/release")
    List<ReleaseRecordDTO> releaseRecordList();

    @ApiOperation(value = "获取某版本所有流新消息")
    @GetMapping(value = "/release/{version:.*}")
    List<ReleaseFlowDTO> triggerFlowList(@PathVariable String version);
}
