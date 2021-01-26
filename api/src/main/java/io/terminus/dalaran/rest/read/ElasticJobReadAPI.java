package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.ElasticJobInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping(value = "/api/job", produces = {"application/json; charset=UTF-8"})
public interface ElasticJobReadAPI {

    @ApiOperation(value = "查询所有elastic-job")
    @GetMapping(value = "/list")
    List<ElasticJobInfo> list();
}
