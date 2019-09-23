package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.common.model.Response;
import io.terminus.dalaran.console.exception.DalaranException;
import io.terminus.dalaran.console.model.ResponseMessage;
import io.terminus.dalaran.console.model.dto.FunctionDTO;
import io.terminus.dalaran.console.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/function")
public class FunctionRest {

    @Autowired
    private FunctionService service;

    @PostMapping
    @ApiOperation("新增函数")
    @DalaranException(value = ResponseMessage.FUNCTION_CREATE_ERROR)
    public Long create(@RequestBody FunctionDTO dto) {
        return service.create(dto);
    }

    @PutMapping
    @ApiOperation("更新函数")
    @DalaranException(value = ResponseMessage.FUNCTION_UPDATE_ERROR)
    public FunctionDTO update(@RequestBody FunctionDTO dto) {
        return service.update(dto);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除函数")
    @DalaranException(value = ResponseMessage.FUNCTION_DELETE_ERROR)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取函数详情")
    @DalaranException(value = ResponseMessage.FUNCTION_QUERY_ERROR)
    public FunctionDTO detail(@PathVariable Long id) {
        return service.detail(id);
    }
}
