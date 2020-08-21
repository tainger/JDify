package io.terminus.dalaran.rest.write;


import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.LimiterDTO;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/limiter", produces = {"application/json; charset=UTF-8"})
public interface LimiterWriteAPI {

    @PostMapping
    @ApiOperation("新增熔断器")
    Long create(@RequestBody LimiterDTO limiterDTO);

    @PutMapping
    @ApiOperation("更新熔断器")
    LimiterDTO update(@RequestBody LimiterDTO limiterDTO);

    @DeleteMapping("/{id}")
    @ApiOperation("删除熔断器")
    void deleteById(@PathVariable Long id);
}
