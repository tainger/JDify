package io.terminus.dalaran.rest.write;


import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.ElasticJobInfo;
import io.terminus.dalaran.response.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/api/job", produces = {"application/json; charset=UTF-8"})
public interface ElasticJobWriteAPI {

    @ApiOperation(value = "触发该任务")
    @PostMapping(value = "/trigger")
    ResponseResult trigger(@RequestBody ElasticJobInfo elasticJobInfo);

    @ApiOperation(value = "失效该任务")
    @PostMapping(value = "/disable")
    ResponseResult disable(@RequestBody ElasticJobInfo elasticJobInfo);

    @ApiOperation(value = "生效该任务")
    @PostMapping(value = "/enable")
    ResponseResult enable(@RequestBody ElasticJobInfo elasticJobInfo);

    @ApiOperation(value = "更新该任务")
    @PostMapping(value = "/update")
    ResponseResult update(@RequestBody ElasticJobInfo elasticJobInfo);

}
