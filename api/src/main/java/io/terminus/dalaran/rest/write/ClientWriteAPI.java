package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.CreateResponse;
import io.terminus.dalaran.model.dto.ClientDTO;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/client", produces = {"application/json; charset=UTF-8"})
public interface ClientWriteAPI {

    @PostMapping
    @ApiOperation("新增客户端")
    CreateResponse create(@RequestBody ClientDTO clientDTO);

    @PutMapping
    @ApiOperation("更新客户端")
    ClientDTO update(@RequestBody ClientDTO clientDTO);

    @DeleteMapping("/{id}")
    @ApiOperation("删除客户端")
    void deleteById(@PathVariable String id);
}
