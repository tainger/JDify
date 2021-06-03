package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.TracingType;
import io.terminus.dalaran.component.elasticjob.trigger.ElasticJobConfig;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.repository.TriggerFlowRepository;
import io.terminus.dalaran.console.service.MonitorService;
import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.core.resource.repository.TracingLogRepository;
import io.terminus.dalaran.model.dto.ElasticJobTaskDetailDTO;
import io.terminus.dalaran.model.query.TracingLogQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MonitorServiceImpl implements MonitorService {

    @Autowired
    private TracingLogRepository tracingLogRepository;

    @Autowired
    private TriggerFlowRepository flowRepository;

    @Autowired
    private TracingLogService logService;

    @Override
    public List<ElasticJobTaskDetailDTO> getTaskByTaskName(String taskName) {
        List<ElasticJobTaskDetailDTO> tasks = new ArrayList<>();
        TriggerFlowEntity flowEntity = flowRepository.findByName(taskName);
        if (flowEntity == null || !StringUtils.equalsIgnoreCase(flowEntity.getTriggerType(), "elastic-job")) {
            return tasks;
        }
        ElasticJobConfig elasticJobConfig = JSON.parseObject(flowEntity.getTriggerConfig(), ElasticJobConfig.class);
        TracingLogQuery logQuery = new TracingLogQuery();
        logQuery.setFlowId(flowEntity.getResourceKey());
        logQuery.setTracingType(TracingType.Flow);
        logService.triggerLogs(logQuery).forEach(log -> {
            ElasticJobTaskDetailDTO taskDetail = new ElasticJobTaskDetailDTO();
            taskDetail.setTaskName(taskName);
            taskDetail.setExecuteTime(log.getElapsed());
            taskDetail.setFireTime(log.getCreatedAt());
            taskDetail.setCron(elasticJobConfig.getCron());
            tasks.add(taskDetail);
        });
        return tasks;
    }

    @Override
    public List<ElasticJobTaskDetailDTO> getAllTask() {
        return null;
    }

}
