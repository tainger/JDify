package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.common.model.Response;
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
    private Response create(@RequestBody FunctionDTO dto) {
        try {
            return Response.ok(service.create(dto));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.FUNCTION_CREATE_ERROR);
        }
    }

    @PutMapping
    @ApiOperation("更新函数")
    private Response update(@RequestBody FunctionDTO dto) {
        try {
            return Response.ok(service.update(dto));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.FUNCTION_UPDATE_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除函数")
    private Response create(@PathVariable Long id) {
        try {
            service.delete(id);
            return Response.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.FUNCTION_DELETE_ERROR);
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("获取函数详情")
    private Response detail(@PathVariable Long id) {
        try {
            return Response.ok(service.detail(id));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.FUNCTION_QUERY_ERROR);
        }
    }
}
