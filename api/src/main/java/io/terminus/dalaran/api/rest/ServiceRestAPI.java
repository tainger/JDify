package io.terminus.dalaran.api.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.ServiceDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "/api/service", produces = {"application/json; charset=UTF-8"})
public interface ServiceRestAPI {


    @PostMapping
    @ApiOperation("新增服务")
    Long create(@RequestBody ServiceDTO serviceDTO);

    @PutMapping
    @ApiOperation("更新服务")
    ServiceDTO update(@RequestBody ServiceDTO serviceDTO);

    @DeleteMapping("/{id}")
    @ApiOperation("删除服务")
    void create(@PathVariable Long id);

    @GetMapping("/{id}")
    @ApiOperation("获取服务详情")
    ServiceDTO detail(@PathVariable Long id);

    @GetMapping
    @ApiOperation("获取所有服务")
    List<ServiceDTO> list();

    @GetMapping("/{id}/operation")
    @ApiOperation("获取服务可选项")
    List<String> operations(@PathVariable Long id);
}
