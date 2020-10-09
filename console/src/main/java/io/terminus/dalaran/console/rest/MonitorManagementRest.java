package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.service.MonitorService;
import io.terminus.dalaran.model.dto.ScheduleTaskDetailDTO;
import io.terminus.dalaran.rest.read.MonitorReadAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MonitorManagementRest implements MonitorReadAPI {

    @Autowired
    private MonitorService monitorService;

    @Override
    public List<ScheduleTaskDetailDTO> getScheduleTaskByName(String name) {
        return monitorService.getTaskByTaskName(name);
    }
}
