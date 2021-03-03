package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.LimiterDTO;
import io.terminus.dalaran.model.dto.basic.BasicLimiterInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping(value = "/api/limiter", produces = {"application/json; charset=UTF-8"})
public interface LimiterReadAPI {

    @GetMapping("/{id}")
    @ApiOperation("获取熔断器详情")
    LimiterDTO detail(@PathVariable String id);

    @GetMapping("/option")
    @ApiOperation("获取熔断器可选项")
    List<BasicLimiterInfo> selectOptions(@RequestParam String limiterType);
}
