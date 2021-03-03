package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.CreateResponse;
import io.terminus.dalaran.model.dto.ConnectorDTO;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/connector", produces = {"application/json; charset=UTF-8"})
public interface ConnectorWriteAPI {

    @PostMapping
    @ApiOperation("新增连接器")
    CreateResponse create(@RequestBody ConnectorDTO connectorDTO);

    @PutMapping
    @ApiOperation("更新连接器")
    ConnectorDTO update(@RequestBody ConnectorDTO connectorDTO);

    @DeleteMapping("/{id}")
    @ApiOperation("删除连接器")
    void deleteById(@PathVariable String id);
}
