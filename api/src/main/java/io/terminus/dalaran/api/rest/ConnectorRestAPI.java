package io.terminus.dalaran.api.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.ComponentType;
import io.terminus.dalaran.model.dto.ConnectorDTO;
import io.terminus.dalaran.model.dto.basic.BasicConnectorInfo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "/api/connector", produces = {"application/json; charset=UTF-8"})
public interface ConnectorRestAPI {

    @PostMapping
    @ApiOperation("新增连接器")
    Long create(@RequestBody ConnectorDTO connectorDTO);

    @PutMapping
    @ApiOperation("更新连接器")
    ConnectorDTO update(@RequestBody ConnectorDTO connectorDTO);

    @DeleteMapping("/{id}")
    @ApiOperation("删除连接器")
    void create(@PathVariable Long id);

    @GetMapping("/{id}")
    @ApiOperation("获取连接器详情")
    ConnectorDTO detail(@PathVariable Long id);

    @GetMapping("/option")
    @ApiOperation("获取连接器可选项")
    List<BasicConnectorInfo> selectOptions(@RequestParam ComponentType componentType, @RequestParam String componentName);
}
