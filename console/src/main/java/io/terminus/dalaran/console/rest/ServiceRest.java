package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.common.model.Response;
import io.terminus.dalaran.console.exception.DalaranException;
import io.terminus.dalaran.console.model.ResponseMessage;
import io.terminus.dalaran.console.model.dto.ServiceDTO;
import io.terminus.dalaran.console.service.ServiceManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service")
public class ServiceRest {

    @Autowired
    private ServiceManagement serviceManagement;

    @PostMapping
    @ApiOperation("新增服务")
    @DalaranException(value = ResponseMessage.SERVICE_CREATE_ERROR)
    public Long create(@RequestBody ServiceDTO serviceDTO) {
        return serviceManagement.create(serviceDTO);
    }

    @PutMapping
    @ApiOperation("更新服务")
    @DalaranException(value = ResponseMessage.SERVICE_UPDATE_ERROR)
    public ServiceDTO update(@RequestBody ServiceDTO serviceDTO) {
        return serviceManagement.update(serviceDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除服务")
    @DalaranException(value = ResponseMessage.SERVICE_DELETE_ERROR)
    public void create(@PathVariable Long id) {
        serviceManagement.delete(id);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取服务详情")
    @DalaranException(value = ResponseMessage.SERVICE_QUERY_ERROR)
    public ServiceDTO detail(@PathVariable Long id) {
        return serviceManagement.detail(id);
    }

    @GetMapping
    @ApiOperation("获取所有服务")
    @DalaranException(value = ResponseMessage.SERVICE_QUERY_ERROR)
    public List<ServiceDTO> list() {
        return serviceManagement.list();
    }

    @GetMapping("/{id}/operation")
    @ApiOperation("获取服务可选项")
    @DalaranException(value = ResponseMessage.SERVICE_QUERY_ERROR)
    public List<String> operations(@PathVariable Long id) {
        return serviceManagement.listOperation(id);
    }
}
