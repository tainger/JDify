package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.market.model.BasicResourceDTO;
import io.terminus.dalaran.model.BasicResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/api/repository/private", produces = {"application/json; charset=UTF-8"})
public interface PrivateRepositoryWriteAPI {

    @ApiOperation(value = "发布模板到市场")
    @PostMapping(value = "/publish")
    BasicResponse publish(@RequestBody BasicResourceDTO basicResource);
}
