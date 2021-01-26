package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.service.JobAPIService;
import io.terminus.dalaran.model.dto.ElasticJobInfo;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobAPIFactory;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobOperateAPI;
import org.springframework.stereotype.Service;

@Service
public class JobAPIServiceImpl implements JobAPIService {

    @Override
    public JobOperateAPI getJobOperatorAPI(ElasticJobInfo elasticJobInfo) {
        return JobAPIFactory.createJobOperateAPI(elasticJobInfo.getServerLists(), elasticJobInfo.getNamespace(), null);

    }
}
