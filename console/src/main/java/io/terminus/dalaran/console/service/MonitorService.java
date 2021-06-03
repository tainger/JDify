package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.ElasticJobTaskDetailDTO;

import java.util.List;

public interface MonitorService {

    List<ElasticJobTaskDetailDTO> getTaskByTaskName(String taskName);

    List<ElasticJobTaskDetailDTO> getAllTask();
}
