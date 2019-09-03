package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.common.model.Response;
import io.terminus.dalaran.console.model.ResponseMessage;
import io.terminus.dalaran.console.model.dto.ServiceDTO;
import io.terminus.dalaran.console.service.ServiceManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/service")
public class ServiceRest {

    @Autowired
    private ServiceManagement serviceManagement;

    @PostMapping
    @ApiOperation("新增服务")
    private Response create(@RequestBody ServiceDTO serviceDTO) {
        try {
            return Response.ok(serviceManagement.create(serviceDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.SERVICE_CREATE_ERROR);
        }
    }

    @PutMapping
    @ApiOperation("更新服务")
    private Response update(@RequestBody ServiceDTO serviceDTO) {
        try {
            return Response.ok(serviceManagement.update(serviceDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.SERVICE_UPDATE_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除服务")
    private Response create(@PathVariable Long id) {
        try {
            serviceManagement.delete(id);
            return Response.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.SERVICE_DELETE_ERROR);
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("获取服务详情")
    private Response detail(@PathVariable Long id) {
        try {
            return Response.ok(serviceManagement.detail(id));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.SERVICE_QUERY_ERROR);
        }
    }

    @GetMapping
    @ApiOperation("获取所有服务")
    private Response list() {
        try {
            return Response.ok(serviceManagement.list());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.SERVICE_QUERY_ERROR);
        }
    }

    @GetMapping("/{id}/operation")
    @ApiOperation("获取服务可选项")
    private Response operations(@PathVariable Long id) {
        try {
            return Response.ok(serviceManagement.listOperation(id));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.SERVICE_QUERY_ERROR);
        }
    }
}
