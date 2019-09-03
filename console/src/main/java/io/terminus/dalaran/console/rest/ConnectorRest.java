package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.common.model.Response;
import io.terminus.dalaran.console.model.ResponseMessage;
import io.terminus.dalaran.console.model.dto.ConnectorDTO;
import io.terminus.dalaran.console.service.ConnectorService;
import io.terminus.dalaran.core.component.ComponentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/connector")
public class ConnectorRest {

    @Autowired
    private ConnectorService connectorService;

    @PostMapping
    @ApiOperation("新增连接器")
    private Response create(@RequestBody ConnectorDTO connectorDTO) {
        try {
            return Response.ok(connectorService.create(connectorDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.CONNECTOR_CREATE_ERROR);
        }
    }

    @PutMapping
    @ApiOperation("更新连接器")
    private Response update(@RequestBody ConnectorDTO connectorDTO) {
        try {
            return Response.ok(connectorService.update(connectorDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.CONNECTOR_UPDATE_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除连接器")
    private Response create(@PathVariable Long id) {
        try {
            connectorService.delete(id);
            return Response.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.CONNECTOR_DELETE_ERROR);
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("获取连接器详情")
    private Response detail(@PathVariable Long id) {
        try {
            return Response.ok(connectorService.detail(id));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.CONNECTOR_QUERY_ERROR);
        }
    }

    @GetMapping("/option")
    @ApiOperation("获取连接器可选项")
    private Response selectOptions(@RequestParam ComponentType componentType, @RequestParam String componentName) {
        try {
            return Response.ok(connectorService.listBasicInfoByComponent(componentType, componentName));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.CONNECTOR_QUERY_ERROR);
        }
    }
}
