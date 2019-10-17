package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.FunctionDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/api/function", produces = {"application/json; charset=UTF-8"})
public interface FunctionReadAPI {

    @GetMapping("/{id}")
    @ApiOperation("获取函数详情")
    FunctionDTO detail(@PathVariable Long id);
}
