package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.ComponentType;
import io.terminus.dalaran.model.dto.ConnectorDTO;
import io.terminus.dalaran.model.dto.basic.BasicConnectorInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping(value = "/api/connector", produces = {"application/json; charset=UTF-8"})
public interface ConnectorReadAPI {

    @GetMapping("/{id}")
    @ApiOperation("获取连接器详情")
    ConnectorDTO detail(@PathVariable Long id);

    @GetMapping("/option")
    @ApiOperation("获取连接器可选项")
    List<BasicConnectorInfo> selectOptions(@RequestParam ComponentType componentType, @RequestParam String componentName);
}
