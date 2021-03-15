package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.TracingType;
import io.terminus.dalaran.console.entity.AlarmRuleEntity;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.repository.AlarmRuleRepository;
import io.terminus.dalaran.console.repository.SubFlowRepository;
import io.terminus.dalaran.console.repository.TriggerFlowRepository;
import io.terminus.dalaran.console.service.ModuleManagementService;
import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.core.resource.entity.basic.BasicFlowEntity;
import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import io.terminus.dalaran.core.resource.entity.common.ReleaseRecordEntity;
import io.terminus.dalaran.core.resource.entity.common.TracingLogEntity;
import io.terminus.dalaran.core.resource.entity.released.TriggerFlowReleasedEntity;
import io.terminus.dalaran.core.resource.repository.*;
import io.terminus.dalaran.model.dto.log.*;
import io.terminus.dalaran.model.query.TracingLogQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;
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

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ReleaseRecordRepository releaseRecordRepository;

    @Autowired
    private TriggerFlowReleasedRepository triggerFlowReleasedRepository;

    @Autowired
    private AlarmRuleRepository alarmRuleRepository;


    private String timeZone = System.getenv("TZ");

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

    @Override
    public DetailLogDTO logDetailById(String flowId) {
        DetailLogDTO detailLogDTO = new DetailLogDTO();
        ReleaseRecordEntity releaseRecordEntity = releaseRecordRepository.findByEnabledTrue();
        if (releaseRecordEntity == null) {
            return detailLogDTO;
        }
        String version = releaseRecordEntity.getVersion();
        TriggerFlowReleasedEntity entity = triggerFlowReleasedRepository.findByVersionAndOriginId(version, flowId);
        if(entity == null) {
            return detailLogDTO;
        }
        detailLogDTO = buildDetailLog(entity, version, flowId);
        List<TracingLogEntity> tracingLogEntity = tracingLogRepository.findByFlowIdAndVersion(flowId, version);
        if (tracingLogEntity.size() == 0) {
            return detailLogDTO;
        }
        TimeLogDTO timeLogDTO= getElapsedTime(flowId, version);
        List<TracingLogEntity> tracingLogFailEntity = tracingLogRepository.findByFlowIdAndVersionAndSuccessful(flowId, version, false);
        Long lastExceptionDate = null;
        if (tracingLogFailEntity.size() != 0) {
            lastExceptionDate = getLastExceptionDate(flowId, version);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isNotBlank(timeZone)) {
            format.setTimeZone(TimeZone.getTimeZone(timeZone));
        } else {
            format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        }
        detailLogDTO.setAvgTime(timeLogDTO.getAvgTime());
        detailLogDTO.setMaxTime(timeLogDTO.getMaxTime());
        //可能会存在有多个相同的最大时间的情况，取时间最早的那个
        detailLogDTO.setMaxTimeRecordId(getMaxTimeRecordId(flowId, version, timeLogDTO.getMaxTime()).get(0).toString());
        if (lastExceptionDate == null) {
            detailLogDTO.setLastExceptionDate(null);
            detailLogDTO.setLastExceptionDateRecordId(null);
        } else {
            detailLogDTO.setLastExceptionDate(format.format(lastExceptionDate));
            detailLogDTO.setLastExceptionDateRecordId(getLastExceptionRecordId(flowId, version, lastExceptionDate));
        }
        return detailLogDTO;
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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isNotBlank(timeZone)) {
            format.setTimeZone(TimeZone.getTimeZone(timeZone));
        } else {
            format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        }
        mainLog.setCreatedAt(format.format(log.getCreatedAt()));
        mainLog.setTimestamp(new Date(log.getTimestamp()));
        mainLog.setElapsed(log.getElapsed());
        mainLog.setInputBody(log.getInputBody());
        mainLog.setInputBodyType(log.getInputBodyType());
        mainLog.setOutputBody(log.getOutputBody());
        mainLog.setOutputBodyType(log.getOutputBodyType());
        mainLog.setSuccessful(log.isSuccessful());
        mainLog.setVersion(log.getVersion());

        if (log.getFlowId() != null) {
            mainLog.setFlowId(log.getFlowId());
            BasicFlowEntity flowEntity = null;
            switch (log.getTracingType()) {
                case Flow:
                case TestFlow:
                    flowEntity = flowRepository.findByResourceKey(log.getFlowId());
                    break;
                case SubFlow:
                case TestSubFlow:
                    flowEntity = subFlowRepository.findByResourceKey(log.getFlowId());
                    break;
            }
            if (flowEntity != null) {
                String moduleName = moduleService.getModuleName(flowEntity.getModuleId());
                mainLog.setFlowName(flowEntity.getName());
                mainLog.setModuleId(flowEntity.getModuleId());
                if(moduleName!=null) {
                    mainLog.setModuleName(moduleName);
                }
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
        tracingLog.setVersion(log.getVersion());

        tracingLog.setProcessorId(log.getProcessorId());
        tracingLog.setFlowId(log.getFlowId());
        TriggerFlowEntity flowEntity = flowRepository.findByResourceKey(log.getFlowId());
        if (flowEntity != null) {
            tracingLog.setFlowName(flowEntity.getName());
        }
        return tracingLog;
    }

    private DetailLogDTO buildDetailLog(TriggerFlowReleasedEntity entity, String version, String flowId) {
        List<ModuleEntity> moduleEntities = moduleRepository.findByIsExistTrue();
        Map<String, String> map = new HashMap<>();
        moduleEntities.forEach(moduleEntity -> map.put(moduleEntity.getResourceKey(), moduleEntity.getName()));
        DetailLogDTO detailLogDTO = new DetailLogDTO();
        detailLogDTO.setName(entity.getName());
        detailLogDTO.setTriggerType(entity.getTriggerType());
        detailLogDTO.setVersion(entity.getVersion());
        detailLogDTO.setModuleName(map.get(entity.getModuleId()));
        detailLogDTO.setOnline(entity.isOnline());
        detailLogDTO.setDescription(entity.getDescription());
        detailLogDTO.setMonitor(entity.isMonitor());
        TriggerFlowEntity triggerFlowEntity = flowRepository.findByResourceKey(flowId);
        AlarmRuleEntity alarmRuleEntity = alarmRuleRepository.findByResourceKey(triggerFlowEntity.getAlarmResourceKey());
        if (alarmRuleEntity != null) {
            detailLogDTO.setMonitorId(alarmRuleEntity.getResourceKey());
        }
        detailLogDTO.setMonitorName(alarmRuleEntity.getName());
        return detailLogDTO;
    }

    public TimeLogDTO getElapsedTime(String flowId, String version) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TimeLogDTO> criteriaQuery = builder.createQuery(TimeLogDTO.class);
        Root<TracingLogEntity> root = criteriaQuery.from(TracingLogEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        if (flowId != null) {
            predicates.add(builder.equal(root.get("flowId"), flowId));
        }
        if (version != null) {
            predicates.add(builder.equal(root.get("version"), version));
        }
        predicates.add(builder.equal(root.get("tracingType"), TracingType.Flow));
        criteriaQuery.multiselect(builder.max(root.get("elapsed")), builder.avg(root.get("elapsed"))).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public List getMaxTimeRecordId(String flowId, String version, Long maxTime) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = builder.createQuery(String.class);
        Root<TracingLogEntity> root = criteriaQuery.from(TracingLogEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        if (flowId != null) {
            predicates.add(builder.equal(root.get("flowId"), flowId));
        }
        if (version != null) {
            predicates.add(builder.equal(root.get("version"), version));
        }
        predicates.add(builder.equal(root.get("tracingType"), TracingType.Flow));
        predicates.add(builder.equal(root.get("elapsed"), maxTime));
        criteriaQuery.multiselect(root.get("recordId")).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public Long getLastExceptionDate(String flowId, String version) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<TracingLogEntity> root = criteriaQuery.from(TracingLogEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        if (flowId != null) {
            predicates.add(builder.equal(root.get("flowId"), flowId));
        }
        if (version != null) {
            predicates.add(builder.equal(root.get("version"), version));
        }
        predicates.add(builder.equal(root.get("tracingType"), TracingType.Flow));
        predicates.add(builder.equal(root.get("successful"), false));
        criteriaQuery.multiselect(builder.max(root.get("timestamp"))).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public String getLastExceptionRecordId(String flowId, String version, Long timeStamp) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = builder.createQuery(String.class);
        Root<TracingLogEntity> root = criteriaQuery.from(TracingLogEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        if (flowId != null) {
            predicates.add(builder.equal(root.get("flowId"), flowId));
        }
        if (version != null) {
            predicates.add(builder.equal(root.get("version"), version));
        }
        predicates.add(builder.equal(root.get("successful"), false));
        predicates.add(builder.equal(root.get("timestamp"), timeStamp));
        criteriaQuery.multiselect(root.get("recordId")).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }


}
