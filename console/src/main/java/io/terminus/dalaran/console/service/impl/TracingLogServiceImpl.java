package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.TracingType;
import io.terminus.dalaran.console.model.TracingLog;
import io.terminus.dalaran.console.model.TracingMainLog;
import io.terminus.dalaran.console.model.query.TracingLogQuery;
import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.entity.FlowEntity;
import io.terminus.dalaran.entity.ProcessorEntity;
import io.terminus.dalaran.entity.TracingLogEntity;
import io.terminus.dalaran.entity.TriggerEntity;
import io.terminus.dalaran.repository.FlowRepository;
import io.terminus.dalaran.repository.ProcessorRepository;
import io.terminus.dalaran.repository.TracingLogRepository;
import io.terminus.dalaran.repository.TriggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TracingLogServiceImpl implements TracingLogService {

    @Autowired
    private TracingLogRepository tracingLogRepository;

    @Autowired
    private FlowRepository flowRepository;

    @Autowired
    private ProcessorRepository processorRepository;

    @Autowired
    private TriggerRepository triggerRepository;

    @Override
    public List<TracingMainLog> triggerLogs(TracingLogQuery query) {
        List<TracingLogEntity> logs = tracingLogRepository.findAll(buildSpecification(query));
        return logs.stream().map(this::buildTracingMainLog).collect(Collectors.toList());
    }

    @Override
    public TracingMainLog getRecordDetail(String recordId) {
        TracingLogEntity testMainLog = tracingLogRepository.findByRecordIdAndMainTrue(recordId);
        if (testMainLog == null) {
            return null;
        }
        TracingMainLog triggerMainLogDetail = buildTracingMainLog(testMainLog);

        List<TracingLogEntity> tracingLogEntityList = tracingLogRepository.findByRecordIdAndTracingType(recordId, TracingType.Flow);
        List<TracingLog> tracingLogs = tracingLogEntityList.stream().map(this::buildTracingLog).collect(Collectors.toList());
        triggerMainLogDetail.setTracingLogList(tracingLogs);
        return triggerMainLogDetail;
    }

    private Specification<TracingLogEntity> buildSpecification(TracingLogQuery query) {
        return (root, query1, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("main"), Boolean.TRUE));
            if (query.isTestFlow()) {
                predicates.add(builder.equal(root.get("tracingType"), TracingType.TestFlow));
            } else {
                predicates.add(builder.equal(root.get("tracingType"), TracingType.Trigger));
            }
            if (query.getSuccessful() != null) {
                predicates.add(builder.equal(root.get("successful"), query.getSuccessful()));
            }
            if (query.getFlowId() != null) {
                predicates.add(builder.equal(root.get("flowId"), query.getFlowId()));
            }
            if (query.getModuleId() != null) {
                predicates.add(builder.equal(root.get("moduleId"), query.getModuleId()));
            }
            if (query.getTriggerId() != null) {
                predicates.add(builder.equal(root.get("triggerId"), query.getTriggerId()));
            }
            if (query.getStartTime() != null) {
                predicates.add(builder.ge(root.get("timestamp"), query.getStartTime().getTime()));
            }
            if (query.getEndTime() != null) {
                predicates.add(builder.le(root.get("timestamp"), query.getEndTime().getTime()));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private TracingMainLog buildTracingMainLog(TracingLogEntity log) {
        TracingMainLog mainLog = new TracingMainLog();
        mainLog.setId(log.getId());
        mainLog.setRecordId(log.getRecordId());
        mainLog.setTimestamp(new Date(log.getTimestamp()));
        mainLog.setElapsed(log.getElapsed());
        mainLog.setTriggerId(log.getTriggerId());
        mainLog.setInputBody(log.getInputBody());
        mainLog.setInputBodyType(log.getInputBodyType());
        mainLog.setOutputBody(log.getOutputBody());
        mainLog.setOutputBodyType(log.getOutputBodyType());
        mainLog.setSuccessful(log.getSuccessful());

        if (log.getTriggerId() != null) {
            TriggerEntity triggerEntity = triggerRepository.findOne(log.getTriggerId());
            if (triggerEntity != null) {
                mainLog.setTriggerName(triggerEntity.getName());
            }
        }
        if (log.getFlowId() != null) {
            FlowEntity flowEntity = flowRepository.findOne(log.getFlowId());
            if (flowEntity != null) {
                mainLog.setFlowName(flowEntity.getName());
            }
        }
        return mainLog;
    }

    private TracingLog buildTracingLog(TracingLogEntity log) {
        TracingLog tracingLog = new TracingLog();
        tracingLog.setId(log.getId());
        tracingLog.setRecordId(log.getRecordId());
        tracingLog.setTimestamp(new Date(log.getTimestamp()));
        tracingLog.setElapsed(log.getElapsed());
        tracingLog.setInputBody(log.getInputBody());
        tracingLog.setInputBodyType(log.getInputBodyType());
        tracingLog.setOutputBody(log.getOutputBody());
        tracingLog.setOutputBodyType(log.getOutputBodyType());
        tracingLog.setSuccessful(log.getSuccessful());

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
        return tracingLog;
    }
}
