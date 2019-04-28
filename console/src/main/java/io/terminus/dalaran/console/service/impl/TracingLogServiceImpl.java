package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.TracingType;
import io.terminus.dalaran.console.model.dto.log.MainLogDTO;
import io.terminus.dalaran.console.model.dto.log.TracingLogDTO;
import io.terminus.dalaran.console.model.query.TracingLogQuery;
import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.entity.TracingLogEntity;
import io.terminus.dalaran.entity.flow.TriggerFlowEntity;
import io.terminus.dalaran.repository.TracingLogRepository;
import io.terminus.dalaran.repository.TriggerFlowRepository;
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
    private TriggerFlowRepository flowRepository;

    @Override
    public List<MainLogDTO> triggerLogs(TracingLogQuery query) {
        List<TracingLogEntity> logs = tracingLogRepository.findAll(buildSpecification(query));
        return logs.stream().map(this::buildTracingMainLog).collect(Collectors.toList());
    }

    @Override
    public MainLogDTO getRecordDetail(String recordId) {
        TracingLogEntity testMainLog = tracingLogRepository.findByRecordIdAndMainTrue(recordId);
        if (testMainLog == null) {
            return null;
        }
        MainLogDTO triggerMainLogDetail = buildTracingMainLog(testMainLog);

        List<TracingLogEntity> tracingLogEntityList = tracingLogRepository.findByRecordIdAndTracingType(recordId, TracingType.Flow);
        List<TracingLogDTO> tracingLogs = tracingLogEntityList.stream().map(this::buildTracingLog).collect(Collectors.toList());
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
                predicates.add(builder.equal(root.get("tracingType"), TracingType.Flow));
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
            if (query.getStartTime() != null) {
                predicates.add(builder.ge(root.get("timestamp"), query.getStartTime().getTime()));
            }
            if (query.getEndTime() != null) {
                predicates.add(builder.le(root.get("timestamp"), query.getEndTime().getTime()));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private MainLogDTO buildTracingMainLog(TracingLogEntity log) {
        MainLogDTO mainLog = new MainLogDTO();
        mainLog.setId(log.getId());
        mainLog.setRecordId(log.getRecordId());
        mainLog.setTimestamp(new Date(log.getTimestamp()));
        mainLog.setElapsed(log.getElapsed());
        mainLog.setInputBody(log.getInputBody());
        mainLog.setInputBodyType(log.getInputBodyType());
        mainLog.setOutputBody(log.getOutputBody());
        mainLog.setOutputBodyType(log.getOutputBodyType());
        mainLog.setSuccessful(log.isSuccessful());
        if (log.getFlowId() != null) {
            TriggerFlowEntity flowEntity = flowRepository.findOne(log.getFlowId());
            if (flowEntity != null) {
                mainLog.setFlowName(flowEntity.getName());
            }
        }
        return mainLog;
    }

    private TracingLogDTO buildTracingLog(TracingLogEntity log) {
        TracingLogDTO tracingLog = new TracingLogDTO();
        tracingLog.setId(log.getId());
        tracingLog.setRecordId(log.getRecordId());
        tracingLog.setTimestamp(new Date(log.getTimestamp()));
        tracingLog.setElapsed(log.getElapsed());
        tracingLog.setInputBody(log.getInputBody());
        tracingLog.setInputBodyType(log.getInputBodyType());
        tracingLog.setOutputBody(log.getOutputBody());
        tracingLog.setOutputBodyType(log.getOutputBodyType());
        tracingLog.setSuccessful(log.isSuccessful());

        tracingLog.setProcessorId(log.getProcessorId());
        tracingLog.setFlowId(log.getProcessorId());
        TriggerFlowEntity flowEntity = flowRepository.findOne(log.getFlowId());
        if (flowEntity != null) {
            tracingLog.setFlowName(flowEntity.getName());
        }
        return tracingLog;
    }
}
