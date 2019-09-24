package io.terminus.dalaran.api.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.ClientDTO;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/client", produces = {"application/json; charset=UTF-8"})
public interface ClientRestAPI {

    @PostMapping
    @ApiOperation("新增客户端")
    Long create(@RequestBody ClientDTO clientDTO);

    @PutMapping
    @ApiOperation("更新客户端")
    ClientDTO update(@RequestBody ClientDTO clientDTO);

    @DeleteMapping("/{id}")
    @ApiOperation("删除客户端")
    void create(@PathVariable Long id);

    @GetMapping("/{id}")
    @ApiOperation("获取客户端详情")
    ClientDTO detail(@PathVariable Long id);
}
