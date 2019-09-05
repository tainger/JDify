package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.common.model.Response;
import io.terminus.dalaran.console.model.ResponseMessage;
import io.terminus.dalaran.console.model.dto.ClientDTO;
import io.terminus.dalaran.console.service.ClientManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
public class ClientManagementRest {

    @Autowired
    private ClientManagementService service;

    @PostMapping
    @ApiOperation("新增客户端")
    private Response create(@RequestBody ClientDTO clientDTO) {
        try {
            return Response.ok(service.create(clientDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.CLIENT_CREATE_ERROR);
        }
    }

    @PutMapping
    @ApiOperation("更新客户端")
    private Response update(@RequestBody ClientDTO clientDTO) {
        try {
            return Response.ok(service.update(clientDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.CLIENT_UPDATE_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除客户端")
    private Response create(@PathVariable Long id) {
        try {
            service.delete(id);
            return Response.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.CLIENT_DELETE_ERROR);
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("获取客户端详情")
    private Response detail(@PathVariable Long id) {
        try {
            return Response.ok(service.detail(id));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.CLIENT_QUERY_ERROR);
        }
    }
}
