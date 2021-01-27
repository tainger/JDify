package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.ElasticJobInfo;
import io.terminus.dalaran.response.ResponseResult;

import java.util.List;

public interface ElasticJobService {

    List<ElasticJobInfo> list();

    ResponseResult trigger(ElasticJobInfo elasticJobInfo);

    ResponseResult disable(ElasticJobInfo elasticJobInfo);

    ResponseResult enable(ElasticJobInfo elasticJobInfo);

    ResponseResult update(ElasticJobInfo elasticJobInfo);
}
