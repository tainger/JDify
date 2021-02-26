package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.terminus.dalaran.console.entity.JobExecutionEntity;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.repository.ElasticJobLogRepository;
import io.terminus.dalaran.console.repository.TriggerFlowRepository;
import io.terminus.dalaran.console.service.ElasticJobService;
import io.terminus.dalaran.console.service.JobAPIService;
import io.terminus.dalaran.core.resource.entity.common.ReleaseRecordEntity;
import io.terminus.dalaran.core.resource.entity.released.TriggerFlowReleasedEntity;
import io.terminus.dalaran.core.resource.repository.ReleaseRecordRepository;
import io.terminus.dalaran.core.resource.repository.TriggerFlowReleasedRepository;
import io.terminus.dalaran.model.dto.ElasticJobConfigInfo;
import io.terminus.dalaran.model.dto.ElasticJobInfo;
import io.terminus.dalaran.model.dto.log.ElasticJobLogDTO;
import io.terminus.dalaran.model.flow.FlowStatus;
import io.terminus.dalaran.model.query.ElasticJobLogQuery;
import io.terminus.dalaran.response.ResponseErrorMsg;
import io.terminus.dalaran.response.ResponseResult;
import org.apache.shardingsphere.elasticjob.infra.pojo.JobConfigurationPOJO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ElasticJobServiceImpl implements ElasticJobService {

    @Autowired
    private ReleaseRecordRepository releaseRecordRepository;

    @Autowired
    private TriggerFlowReleasedRepository releasedTriggerFlowRepository;

    @Autowired
    private TriggerFlowRepository triggerFlowRepository;

    @Autowired
    private ElasticJobLogRepository elasticJobLogRepository;

    @Autowired
    private JobAPIService jobAPIService;

    @Override
    public List<ElasticJobInfo> list() {
        List<TriggerFlowEntity> triggerFlowEntities = triggerFlowRepository.findByStatusNotAndTriggerTypeAndIsExistTrue(FlowStatus.Error, "elastic-job");
        if (triggerFlowEntities == null) {
            return null;
        }
        return triggerFlowEntities.stream().map(this::buildJobInfo).collect(Collectors.toList());
    }

    @Override
    public ResponseResult trigger(ElasticJobInfo elasticJobInfo) {
        try {
            jobAPIService.getJobOperatorAPI(elasticJobInfo).trigger(elasticJobInfo.getJobName());
            return success();
        } catch (Exception e) {
            e.printStackTrace();
            return fail(ResponseErrorMsg.TRIGGER_JOB_ERROR);
        }
    }

    @Override
    public Page<ElasticJobLogDTO> jobLog(ElasticJobLogQuery elasticJobLogQuery, Integer pageNumber, Integer pageSize) {
        Sort order = new Sort(Sort.Direction.DESC, "startTime");
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, order);
        Page<JobExecutionEntity> page = elasticJobLogRepository.findAll(buildSpecification(elasticJobLogQuery), pageable);
        return new PageImpl<>(page.stream().map(this::buildJobLog).collect(Collectors.toList()), pageable, page.getTotalElements());
    }

    public ResponseResult updateStatus(ElasticJobInfo elasticJobInfo, boolean isOnline) {
        Long flowId = elasticJobInfo.getFlowId();
        if (flowId == null) {
            return fail(ResponseErrorMsg.FLOW_ID_NULL);
        }
        Optional<TriggerFlowEntity> triggerFlowEntityOptional = triggerFlowRepository.findById(flowId);
        if (!triggerFlowEntityOptional.isPresent()) {
            return fail(ResponseErrorMsg.FLOW_IS_NULL);
        }
        TriggerFlowEntity triggerFlowEntity = triggerFlowEntityOptional.get();
        triggerFlowEntity.setOnline(isOnline);
        triggerFlowRepository.save(triggerFlowEntity);
        return success();
    }

    private ResponseResult fail(String errorMsg) {
        ResponseResult result = new ResponseResult();
        result.setSuccess(false);
        result.setErrorMsg(errorMsg);
        return result;
    }

    private ResponseResult success() {
        ResponseResult result = new ResponseResult();
        result.setSuccess(true);
        return result;
    }

    private ElasticJobInfo buildJobInfo(TriggerFlowEntity entity) {
        ElasticJobInfo elasticJobInfo;
        elasticJobInfo = JSONObject.parseObject(entity.getTriggerConfig(), ElasticJobInfo.class);
        elasticJobInfo.setFlowId(entity.getId());
        elasticJobInfo.setOnline(entity.isOnline());
        return elasticJobInfo;
    }

    private JobConfigurationPOJO buildJobConfig(ElasticJobInfo info) {
        JobConfigurationPOJO jobConfigurationPOJO = new JobConfigurationPOJO();
        jobConfigurationPOJO.setJobName(info.getJobName());
        jobConfigurationPOJO.setCron(info.getCron());
        jobConfigurationPOJO.setShardingTotalCount(info.getShardingTotalCount());
        return jobConfigurationPOJO;
    }

    private Specification<JobExecutionEntity> buildSpecification(ElasticJobLogQuery query) {
        return (root, query1, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query.getJobName() != null) {
                predicates.add(builder.equal(root.get("jobName"), query.getJobName()));
            }
            if (query.getIp() != null) {
                predicates.add(builder.equal(root.get("ip"), query.getIp()));
            }
            if (query.getStartTimeBegin() != null && query.getStartTimeEnd() != null) {
                predicates.add(builder.between(root.get("startTime"), query.getStartTimeBegin(), query.getStartTimeEnd()));
            }
            if (query.getIsSuccess() != null) {
                predicates.add(builder.equal(root.get("isSuccess"), query.getIsSuccess()));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private ElasticJobLogDTO buildJobLog(JobExecutionEntity entity) {
        ElasticJobLogDTO elasticJobLogDTO = new ElasticJobLogDTO();
        BeanUtils.copyProperties(entity, elasticJobLogDTO);
        return elasticJobLogDTO;
    }

}
