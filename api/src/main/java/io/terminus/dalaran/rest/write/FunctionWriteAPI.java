package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.FunctionDTO;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/function", produces = {"application/json; charset=UTF-8"})
public interface FunctionWriteAPI {

    @PostMapping
    @ApiOperation("新增函数")
    Long create(@RequestBody FunctionDTO dto);

    @PutMapping
    @ApiOperation("更新函数")
    FunctionDTO update(@RequestBody FunctionDTO dto);

    @DeleteMapping("/{id}")
    @ApiOperation("删除函数")
    void deleteById(@PathVariable Long id);
}
