package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.TracingType;
import io.terminus.dalaran.console.model.TracingLog;
import io.terminus.dalaran.console.model.TriggerLog;
import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.entity.FlowEntity;
import io.terminus.dalaran.entity.ProcessorEntity;
import io.terminus.dalaran.entity.TriggerEntity;
import io.terminus.dalaran.model.DalaranTracingLog;
import io.terminus.dalaran.repository.FlowRepository;
import io.terminus.dalaran.repository.ProcessorRepository;
import io.terminus.dalaran.repository.TriggerRepository;
import io.terminus.dalaran.trace.DalaranTracingLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TracingLogServiceImpl implements TracingLogService {

    @Autowired
    private DalaranTracingLogRepository tracingLogRepository;

    @Autowired
    private FlowRepository flowRepository;

    @Autowired
    private ProcessorRepository processorRepository;

    @Autowired
    private TriggerRepository triggerRepository;

    @Override
    public List<TriggerLog> triggerLogs(Long triggerId) {
        List<DalaranTracingLog> logs = tracingLogRepository.findByTriggerIdAndTracingType(triggerId, TracingType.Trigger);
        return logs.stream().map(log -> {
            TriggerLog triggerLog = new TriggerLog();
            triggerLog.setId(log.getId());
            triggerLog.setRecordId(log.getRecordId());
            triggerLog.setTimestamp(new Date(log.getTimestamp()));
            triggerLog.setElapsed(log.getElapsed());
            triggerLog.setTriggerId(log.getTriggerId());
            TriggerEntity triggerEntity = triggerRepository.findOne(log.getTriggerId());
            if (triggerEntity != null) {
                triggerLog.setTriggerName(triggerEntity.getName());
            }
            triggerLog.setInputBody(log.getInputBody());
            triggerLog.setInputBodyType(log.getInputBodyType());
            triggerLog.setOutputBody(log.getOutputBody());
            triggerLog.setOutputBodyType(log.getOutputBodyType());
            return triggerLog;
        }).collect(Collectors.toList());
    }

    @Override
    public List<TracingLog> triggerTracingLogs(Long triggerLogId) {
        DalaranTracingLog triggerLog = tracingLogRepository.findOne(triggerLogId);
        List<DalaranTracingLog> tracingLogs = tracingLogRepository.findByRecordIdAndTracingType(triggerLog.getRecordId(), TracingType.Flow);
        return tracingLogs.stream().map(log -> {
            TracingLog tracingLog = new TracingLog();
            tracingLog.setId(log.getId());
            tracingLog.setRecordId(log.getRecordId());
            tracingLog.setTimestamp(new Date(log.getTimestamp()));
            tracingLog.setElapsed(log.getElapsed());
            tracingLog.setProcessorId(log.getProcessorId());
            ProcessorEntity processorEntity = processorRepository.findOne(log.getProcessorId());
            if (processorEntity != null) {
                tracingLog.setProcessorName(processorEntity.getName());
            }
            tracingLog.setFlowId(log.getProcessorId());
            FlowEntity flowEntity = flowRepository.findOne(log.getFlowId());
            if (flowEntity != null) {
                tracingLog.setFlowName(flowEntity.getName());
            }
            tracingLog.setInputBody(log.getInputBody());
            tracingLog.setInputBodyType(log.getInputBodyType());
            tracingLog.setOutputBody(log.getOutputBody());
            tracingLog.setOutputBodyType(log.getOutputBodyType());
            return tracingLog;
        }).collect(Collectors.toList());
    }

    @Override
    public List<TriggerLog> failedLog() {
        return null;
    }

    @Override
    public List<TriggerLog> successfulLog() {
        return null;
    }
}
