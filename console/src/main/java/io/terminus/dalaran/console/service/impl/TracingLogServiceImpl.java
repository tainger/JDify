package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.TracingType;
import io.terminus.dalaran.console.model.TracingLog;
import io.terminus.dalaran.console.model.TriggerLog;
import io.terminus.dalaran.console.model.query.TracingLogQuery;
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
    private DalaranTracingLogRepository tracingLogRepository;

    @Autowired
    private FlowRepository flowRepository;

    @Autowired
    private ProcessorRepository processorRepository;

    @Autowired
    private TriggerRepository triggerRepository;

    @Override
    public List<TriggerLog> triggerLogs(TracingLogQuery query) {
        List<DalaranTracingLog> logs = tracingLogRepository.findAll(buildSpecification(query));
        return logs.stream().map(this::buildTriggerLog).collect(Collectors.toList());
    }

    @Override
    public TriggerLog getTriggerLogDetail(Long logId) {
        DalaranTracingLog triggerLog = tracingLogRepository.findOne(logId);
        TriggerLog triggerLogDetail = buildTriggerLog(triggerLog);
        List<DalaranTracingLog> tracingLogEntityList = tracingLogRepository.findByRecordIdAndTracingType(triggerLog.getRecordId(), TracingType.Flow);
        List<TracingLog> tracingLogs = tracingLogEntityList.stream().map(this::buildTracingLog).collect(Collectors.toList());
        triggerLogDetail.setTracingLogList(tracingLogs);
        return triggerLogDetail;
    }

    @Override
    public List<TriggerLog> failedLog() {
        // TODO failed log
        return null;
    }

    @Override
    public List<TriggerLog> successfulLog() {
        // TODO successful log
        return null;
    }

    private Specification<DalaranTracingLog> buildSpecification(TracingLogQuery query) {
        return (root, query1, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("tracingType"), TracingType.Trigger));
            predicates.add(builder.equal(root.get("testFlow"), query.isTestFlow()));
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

    private TriggerLog buildTriggerLog(DalaranTracingLog log) {
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
    }

    private TracingLog buildTracingLog(DalaranTracingLog log) {
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
    }
}
