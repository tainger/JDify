package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.console.model.dto.ConnectorDTO;
import io.terminus.dalaran.console.model.dto.basic.BasicConnectorInfo;
import io.terminus.dalaran.console.service.ConnectorService;
import io.terminus.dalaran.core.component.ComponentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connector")
public class ConnectorRest {

    @Autowired
    private ConnectorService connectorService;

    @PostMapping
    @ApiOperation("新增连接器")
    private Long create(@RequestBody ConnectorDTO connectorDTO) {
        return connectorService.create(connectorDTO);
    }

    @PutMapping
    @ApiOperation("更新连接器")
    private ConnectorDTO update(@RequestBody ConnectorDTO connectorDTO) {
        return connectorService.update(connectorDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除连接器")
    private void create(@PathVariable Long id) {
        connectorService.delete(id);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取连接器详情")
    private ConnectorDTO detail(@PathVariable Long id) {
        return connectorService.detail(id);
    }

    @GetMapping("/option")
    @ApiOperation("获取连接器可选项")
    private List<BasicConnectorInfo> selectOptions(@RequestParam ComponentType componentType, @RequestParam String componentName) {
        return connectorService.listBasicInfoByComponent(componentType, componentName);
    }
}
