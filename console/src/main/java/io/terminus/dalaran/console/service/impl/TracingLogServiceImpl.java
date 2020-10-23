package io.terminus.dalaran.console.service.impl;

import com.sun.org.apache.xerces.internal.parsers.SecurityConfiguration;
import io.terminus.dalaran.TracingType;
import io.terminus.dalaran.console.entity.SubFlowEntity;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.repository.SubFlowRepository;
import io.terminus.dalaran.console.repository.TriggerFlowRepository;
import io.terminus.dalaran.console.service.ModuleManagementService;
import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.core.resource.entity.basic.BasicFlowEntity;
import io.terminus.dalaran.core.resource.entity.common.TracingLogEntity;
import io.terminus.dalaran.core.resource.repository.TracingLogRepository;
import io.terminus.dalaran.model.dto.log.MainLogDTO;
import io.terminus.dalaran.model.dto.log.TimeLogDTO;
import io.terminus.dalaran.model.dto.log.TracingLogDTO;
import io.terminus.dalaran.model.query.TracingLogQuery;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TracingLogServiceImpl implements TracingLogService {

    @Autowired
    private TracingLogRepository tracingLogRepository;

    @Autowired
    private TriggerFlowRepository flowRepository;

    @Autowired
    private SubFlowRepository subFlowRepository;

    @Autowired
    private ModuleManagementService moduleService;

    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<MainLogDTO> triggerLogsPageable(TracingLogQuery query, Integer pageNumber, Integer pageSize) {
        Sort order = new Sort(new Sort.Order(Sort.Direction.DESC, "timestamp"));
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, order);
        Page<TracingLogEntity> logs = tracingLogRepository.findAll(buildSpecification(query), pageable);
        return new PageImpl<>(logs.stream().map(this::buildTracingMainLog).collect(Collectors.toList()), pageable, logs.getTotalElements());
    }

    @Override
    public List<MainLogDTO> triggerLogs(TracingLogQuery query) {
        Sort order = new Sort(new Sort.Order(Sort.Direction.DESC, "timestamp"));
        List<TracingLogEntity> logs = tracingLogRepository.findAll(buildSpecification(query), order);
        return logs.stream().map(this::buildTracingMainLog).collect(Collectors.toList());
    }

    @Override
    public TimeLogDTO getElapsedTime(TracingLogQuery query){
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TimeLogDTO> criteriaQuery = builder.createQuery(TimeLogDTO.class);
        Root<TracingLogEntity> root = criteriaQuery.from(TracingLogEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        if(query.getFlowId()!=null){
            predicates.add(builder.equal(root.get("flowId"),query.getFlowId()));
        }
        if(query.getSuccessful()!=null){
            predicates.add(builder.equal(root.get("successful"),query.getSuccessful()));
        }
        if(query.getStartTime()!=null){
            predicates.add(builder.ge(root.get("timestamp"),query.getStartTime().getTime()));
        }
        if(query.getEndTime()!=null){
            predicates.add(builder.le(root.get("timestamp"),query.getEndTime().getTime()));
        }
        criteriaQuery.multiselect(builder.max(root.get("elapsed")),builder.min(root.get("elapsed")),
                builder.avg(root.get("elapsed")),builder.count(root.get("elapsed"))).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public MainLogDTO getRecordDetail(String recordId) {
        TracingLogEntity testMainLog = tracingLogRepository.findByRecordIdAndMainTrue(recordId);
        if (testMainLog == null) {
            return null;
        }
        MainLogDTO triggerMainLogDetail = buildTracingMainLog(testMainLog);

        List<TracingLogEntity> tracingLogEntityList = tracingLogRepository.findByRecordIdAndTracingType(recordId, TracingType.Processor);
        List<TracingLogDTO> tracingLogs = tracingLogEntityList.stream().map(this::buildTracingLog).collect(Collectors.toList());
        triggerMainLogDetail.setTracingLogList(tracingLogs);
        return triggerMainLogDetail;
    }

    private Specification<TracingLogEntity> buildSpecification(TracingLogQuery query) {
        return (root, query1, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("main"), Boolean.TRUE));
            if (query.getSuccessful() != null) {
                predicates.add(builder.equal(root.get("successful"), query.getSuccessful()));
            }
            if (query.getFlowId() != null) {
                predicates.add(builder.equal(root.get("flowId"), query.getFlowId()));
            }
            if (query.getTracingType() != null) {
                predicates.add(builder.equal(root.get("tracingType"), query.getTracingType()));
            } else if (!query.isTestFlow()) {
                predicates.add(builder.and(builder.equal(root.get("tracingType"), TracingType.Flow)));
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
            if (query.getTimeLt() != null) {
                predicates.add(builder.le(root.get("elapsed"), query.getTimeLt()));
            }
            if (query.getTimeGt() != null) {
                predicates.add(builder.ge(root.get("elapsed"), query.getTimeGt()));
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
            mainLog.setFlowId(log.getFlowId());
            BasicFlowEntity flowEntity = null;
            switch (log.getTracingType()) {
                case Flow:
                case TestFlow:
                    Optional<TriggerFlowEntity> triggerFlowEntity = flowRepository.findById(log.getFlowId());
                    if (triggerFlowEntity.isPresent()) {
                        flowEntity = triggerFlowEntity.get();
                    }
                    break;
                case SubFlow:
                case TestSubFlow:
                    Optional<SubFlowEntity> subFlowEntity = subFlowRepository.findById(log.getFlowId());
                    if (subFlowEntity.isPresent()) {
                        flowEntity = subFlowEntity.get();
                    }
                    break;
            }
            if (flowEntity != null) {
                mainLog.setFlowName(flowEntity.getName());
                mainLog.setModuleId(flowEntity.getModuleId());
                mainLog.setModuleName(moduleService.getModuleName(flowEntity.getModuleId()));
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
        tracingLog.setFlowId(log.getFlowId());
        Optional<TriggerFlowEntity> optional = flowRepository.findById(log.getFlowId());
        TriggerFlowEntity flowEntity = null;
        if (optional.isPresent()) {
            flowEntity = optional.get();
        }
        if (flowEntity != null) {
            tracingLog.setFlowName(flowEntity.getName());
        }
        return tracingLog;
    }
}
