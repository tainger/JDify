package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.common.model.Response;
import io.terminus.dalaran.console.exception.DalaranException;
import io.terminus.dalaran.console.model.ResponseMessage;
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
    @DalaranException(value = ResponseMessage.CONNECTOR_CREATE_ERROR)
    public Long create(@RequestBody ConnectorDTO connectorDTO) {
        return connectorService.create(connectorDTO);
    }

    @PutMapping
    @ApiOperation("更新连接器")
    @DalaranException(value = ResponseMessage.CONNECTOR_UPDATE_ERROR)
    public ConnectorDTO update(@RequestBody ConnectorDTO connectorDTO) {
        return connectorService.update(connectorDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除连接器")
    @DalaranException(value = ResponseMessage.CONNECTOR_DELETE_ERROR)
    public void create(@PathVariable Long id) {
        connectorService.delete(id);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取连接器详情")
    @DalaranException(value = ResponseMessage.CONNECTOR_QUERY_ERROR)
    public ConnectorDTO detail(@PathVariable Long id) {
        return connectorService.detail(id);
    }

    @GetMapping("/option")
    @ApiOperation("获取连接器可选项")
    @DalaranException(value = ResponseMessage.CONNECTOR_QUERY_ERROR)
    public List<BasicConnectorInfo> selectOptions(@RequestParam ComponentType componentType, @RequestParam String componentName) {
        return connectorService.listBasicInfoByComponent(componentType, componentName);
    }
}
