package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.ElasticJobInfo;
import io.terminus.dalaran.model.dto.log.ElasticJobLogDTO;
import io.terminus.dalaran.model.query.ElasticJobLogQuery;
import io.terminus.dalaran.response.ResponseResult;
import org.springframework.data.domain.*;

import java.util.List;

public interface ElasticJobService {

    List<ElasticJobInfo> list();

    ResponseResult trigger(ElasticJobInfo elasticJobInfo);

    Page<ElasticJobLogDTO> jobLog(ElasticJobLogQuery elasticJobLogQuery, Integer pageNumber, Integer pageSize);
}
