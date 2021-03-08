package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.CreateResponse;
import io.terminus.dalaran.model.dto.ServiceDTO;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/service", produces = {"application/json; charset=UTF-8"})
public interface ServiceWriteAPI {

    @PostMapping
    @ApiOperation("新增服务")
    CreateResponse create(@RequestBody ServiceDTO serviceDTO);

    @PutMapping
    @ApiOperation("更新服务")
    ServiceDTO update(@RequestBody ServiceDTO serviceDTO);

    @DeleteMapping("/{id}")
    @ApiOperation("删除服务")
    void deleteById(@PathVariable String id);
}
