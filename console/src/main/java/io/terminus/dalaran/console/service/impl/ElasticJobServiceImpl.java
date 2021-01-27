package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.terminus.dalaran.console.service.ElasticJobService;
import io.terminus.dalaran.console.service.JobAPIService;
import io.terminus.dalaran.core.resource.entity.common.ReleaseRecordEntity;
import io.terminus.dalaran.core.resource.entity.released.TriggerFlowReleasedEntity;
import io.terminus.dalaran.core.resource.repository.ReleaseRecordRepository;
import io.terminus.dalaran.core.resource.repository.TriggerFlowReleasedRepository;
import io.terminus.dalaran.model.dto.ElasticJobInfo;
import io.terminus.dalaran.model.flow.FlowStatus;
import io.terminus.dalaran.response.ResponseErrorMsg;
import io.terminus.dalaran.response.ResponseResult;
import org.apache.shardingsphere.elasticjob.infra.pojo.JobConfigurationPOJO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElasticJobServiceImpl implements ElasticJobService {

    @Autowired
    private ReleaseRecordRepository releaseRecordRepository;

    @Autowired
    private TriggerFlowReleasedRepository releasedTriggerFlowRepository;

    @Autowired
    private JobAPIService jobAPIService;

    @Override
    public List<ElasticJobInfo> list() {
        List<TriggerFlowReleasedEntity> triggerFlowReleasedEntity = releasedTriggerFlowRepository.findByVersionAndStatusNotAndTriggerType(getCurrentVersion(), FlowStatus.Error, "elastic-job");
        if (triggerFlowReleasedEntity == null) {
            return null;
        }
        return triggerFlowReleasedEntity.stream().map(this::buildJobInfo).collect(Collectors.toList());
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
    public ResponseResult disable(ElasticJobInfo elasticJobInfo) {
        try {
            jobAPIService.getJobOperatorAPI(elasticJobInfo).disable(elasticJobInfo.getJobName(), null);
            return success();
        } catch (Exception e) {
            e.printStackTrace();
            return fail(ResponseErrorMsg.DISABLE_JOB_ERROR);
        }
    }

    @Override
    public ResponseResult enable(ElasticJobInfo elasticJobInfo) {
        try {
            jobAPIService.getJobOperatorAPI(elasticJobInfo).enable(elasticJobInfo.getJobName(), null);
            return success();
        } catch (Exception e) {
            e.printStackTrace();
            return fail(ResponseErrorMsg.ENABLE_JOB_ERROR);
        }
    }

    @Override
    public ResponseResult update(ElasticJobInfo elasticJobInfo) {
        try {
            jobAPIService.getJobConfigurationAPI(elasticJobInfo).updateJobConfiguration(buildJobConfig(elasticJobInfo));
            return success();
        } catch (Exception e) {
            e.printStackTrace();
            return fail(ResponseErrorMsg.UPDATE_JOB_ERROR);
        }
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

    public String getCurrentVersion() {
        ReleaseRecordEntity recordEntity = releaseRecordRepository.findByEnabledTrue();
        return recordEntity.getVersion();
    }

    private ElasticJobInfo buildJobInfo(TriggerFlowReleasedEntity entity) {
        ElasticJobInfo elasticJobInfo;
        elasticJobInfo = JSONObject.parseObject(entity.getTriggerConfig(), ElasticJobInfo.class);
        elasticJobInfo.setFlowId(entity.getOriginId());
        elasticJobInfo.setJobStatus(entity.getStatus());
        return elasticJobInfo;
    }

    private JobConfigurationPOJO buildJobConfig(ElasticJobInfo info) {
        JobConfigurationPOJO jobConfigurationPOJO = new JobConfigurationPOJO();
        jobConfigurationPOJO.setJobName(info.getJobName());
        jobConfigurationPOJO.setCron(info.getCron());
        jobConfigurationPOJO.setShardingTotalCount(info.getShardingTotalCount());
        return jobConfigurationPOJO;
    }

}
