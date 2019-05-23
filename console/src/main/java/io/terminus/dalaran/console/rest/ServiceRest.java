package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
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
    private Long create(@RequestBody ServiceDTO serviceDTO) {
        return serviceManagement.create(serviceDTO);
    }

    @PutMapping
    @ApiOperation("更新服务")
    private ServiceDTO update(@RequestBody ServiceDTO serviceDTO) {
        return serviceManagement.update(serviceDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除服务")
    private void create(@PathVariable Long id) {
        serviceManagement.delete(id);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取服务详情")
    private ServiceDTO detail(@PathVariable Long id) {
        return serviceManagement.detail(id);
    }

    @GetMapping
    @ApiOperation("获取所有服务")
    private List<ServiceDTO> list() {
        return serviceManagement.list();
    }

    @GetMapping("/{id}/operation")
    @ApiOperation("获取服务可选项")
    private List<String> operations(@PathVariable Long id) {
        return serviceManagement.listOperation(id);
    }
}
