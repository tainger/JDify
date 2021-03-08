package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.repository.TriggerFlowRepository;
import io.terminus.dalaran.console.service.ElasticJobService;
import io.terminus.dalaran.console.service.JobAPIService;
import io.terminus.dalaran.core.resource.repository.ReleaseRecordRepository;
import io.terminus.dalaran.core.resource.repository.TriggerFlowReleasedRepository;
import io.terminus.dalaran.model.dto.ElasticJobInfo;
import io.terminus.dalaran.model.flow.FlowStatus;
import io.terminus.dalaran.response.ResponseErrorMsg;
import io.terminus.dalaran.response.ResponseResult;
import org.apache.shardingsphere.elasticjob.infra.pojo.JobConfigurationPOJO;
import org.springframework.beans.factory.annotation.Autowired;
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
    private TriggerFlowRepository triggerFlowRepository;

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
            return fail(ResponseErrorMsg.TRIGGER_FLOW_ERROR);
        }
    }

    public ResponseResult updateStatus(ElasticJobInfo elasticJobInfo, boolean isOnline) {
        String flowId = elasticJobInfo.getFlowId();
        if (flowId == null) {
            return fail(ResponseErrorMsg.FLOW_ID_NULL);
        }
        TriggerFlowEntity triggerFlowEntity = triggerFlowRepository.findByResourceKey(flowId);
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
        elasticJobInfo.setFlowId(entity.getResourceKey());
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

}
