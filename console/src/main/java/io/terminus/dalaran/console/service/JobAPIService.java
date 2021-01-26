package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.ElasticJobInfo;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobOperateAPI;

public interface JobAPIService {

    JobOperateAPI getJobOperatorAPI(ElasticJobInfo elasticJobInfo);

}
