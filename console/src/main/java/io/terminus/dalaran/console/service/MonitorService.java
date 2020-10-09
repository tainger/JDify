package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.ScheduleTaskDetailDTO;

import java.util.List;

public interface MonitorService {

    List<ScheduleTaskDetailDTO> getTaskByTaskName(String taskName);

    List<ScheduleTaskDetailDTO> getAllTask();
}
