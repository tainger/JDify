package io.terminus.dalaran.api.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.FunctionDTO;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/function", produces = {"application/json; charset=UTF-8"})
public interface FunctionRestAPI {

    @PostMapping
    @ApiOperation("新增函数")
    Long create(@RequestBody FunctionDTO dto);

    @PutMapping
    @ApiOperation("更新函数")
    FunctionDTO update(@RequestBody FunctionDTO dto);

    @DeleteMapping("/{id}")
    @ApiOperation("删除函数")
    void delete(@PathVariable Long id);

    @GetMapping("/{id}")
    @ApiOperation("获取函数详情")
    FunctionDTO detail(@PathVariable Long id);
}
