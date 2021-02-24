package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.service.ElasticJobService;
import io.terminus.dalaran.model.dto.ElasticJobInfo;
import io.terminus.dalaran.model.dto.log.ElasticJobLogDTO;
import io.terminus.dalaran.model.query.ElasticJobLogQuery;
import io.terminus.dalaran.response.ResponseResult;
import io.terminus.dalaran.rest.read.ElasticJobReadAPI;
import io.terminus.dalaran.rest.write.ElasticJobWriteAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ElasticJobRest implements ElasticJobReadAPI, ElasticJobWriteAPI {

    @Autowired
    private ElasticJobService elasticJobService;

    @Override
    public List<ElasticJobInfo> list() {
        return elasticJobService.list();
    }

    @Override
    public Page<ElasticJobLogDTO> jobLog(ElasticJobLogQuery query, Integer pageNumber, Integer pageSize) {
        return elasticJobService.jobLog(query, pageNumber, pageSize);
    }

    @Override
    public ResponseResult trigger(@RequestBody ElasticJobInfo elasticJobInfo) {
        return elasticJobService.trigger(elasticJobInfo);
    }

    @Override
    public ResponseResult disable(@RequestBody ElasticJobInfo elasticJobInfo) {
        return elasticJobService.disable(elasticJobInfo);
    }

    @Override
    public ResponseResult enable(@RequestBody ElasticJobInfo elasticJobInfo) {
        return elasticJobService.enable(elasticJobInfo);
    }
}
