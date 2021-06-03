package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.ElasticJobTaskDetailDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping(value = "/api/monitor/scheduler", produces = {"application/json; charset=UTF-8"})
public interface MonitorReadAPI {

    @ApiOperation(value = "获取定时任务详情")
    @GetMapping(value = "/{name}")
    List<ElasticJobTaskDetailDTO> getScheduleTaskByName(@PathVariable String name);
}
