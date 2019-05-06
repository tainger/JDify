package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.console.model.ReleaseRequestDTO;
import io.terminus.dalaran.console.model.dto.ReleaseRecordDTO;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.service.ReleaseService;
import io.terminus.dalaran.model.config.ConnectorInfo;
import io.terminus.dalaran.model.config.ProcessorInfo;
import io.terminus.dalaran.model.config.TriggerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/platform")
public class PlatformRest {

    @Autowired
    private DalaranContext dalaranContext;

    @Autowired
    private ReleaseService releaseService;

    @ApiOperation(value = "获取某版本所有流新消息")
    @GetMapping(value = "/release")
    private List<ReleaseRecordDTO> releaseRecordList() {
        return releaseService.listReleaseRecordDTO();
    }

    @ApiOperation(value = "获取某版本所有流新消息")
    @GetMapping(value = "/release/{version:.*}")
    private List<TriggerFlowDTO> triggerFlowList(@PathVariable String version) {
        return releaseService.listReleasedTriggerFlowDTO(version);
    }

    @ApiOperation(value = "发布最新的所有内容")
    @PostMapping(value = "/release")
    private ReleaseRecordDTO release(@RequestBody ReleaseRequestDTO requestDTO) {
        return releaseService.release(requestDTO);
    }

    @ApiOperation(value = "回滚至具体版本")
    @PostMapping(value = "/release/{version:.*}/rollback")
    private ReleaseRecordDTO rollback(@PathVariable String version) {
        return releaseService.rollback(version);
    }

    @ApiOperation(value = "获取处理器配置结构")
    @GetMapping(value = "/processor")
    private Collection<ProcessorInfo> listProcessorInfo() {
        return dalaranContext.getDalaranComponentContext().getAllProcessorInfo();
    }

    @ApiOperation(value = "获取触发器配置结构")
    @GetMapping(value = "/trigger")
    private Collection<TriggerInfo> listTriggerInfo() {
        return dalaranContext.getDalaranComponentContext().getAllTriggerInfo();
    }

    @ApiOperation(value = "获取连接器配置结构")
    @GetMapping(value = "/connector")
    private Collection<ConnectorInfo> listConnectorInfo() {
        return dalaranContext.getDalaranComponentContext().getAllConnectorInfo();
    }
}
