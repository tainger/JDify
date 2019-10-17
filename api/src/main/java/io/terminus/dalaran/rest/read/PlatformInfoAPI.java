package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.config.ConnectorInfo;
import io.terminus.dalaran.config.ProcessorInfo;
import io.terminus.dalaran.config.ServiceInfo;
import io.terminus.dalaran.config.TriggerInfo;
import io.terminus.dalaran.model.function.MappingFunctionInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

@RequestMapping(value = "/api/platform", produces = {"application/json; charset=UTF-8"})
public interface PlatformInfoAPI {

    @ApiOperation(value = "获取处理器配置结构")
    @GetMapping(value = "/processor")
    Collection<ProcessorInfo> listProcessorInfo();

    @ApiOperation(value = "获取触发器配置结构")
    @GetMapping(value = "/trigger")
    Collection<TriggerInfo> listTriggerInfo();

    @ApiOperation(value = "获取静态 MappingFunction 信息")
    @GetMapping(value = "/mappingFunctions")
    Collection<MappingFunctionInfo> mappingFunctions();

    @ApiOperation(value = "获取连接器配置结构")
    @GetMapping(value = "/connector")
    Collection<ConnectorInfo> listConnectorInfo();

    @ApiOperation(value = "获取服务配置结构")
    @GetMapping(value = "/service")
    Collection<ServiceInfo> listServiceInfo();

    @ApiOperation(value = "获取可用服务类型")
    @GetMapping(value = "/modelType")
    Collection<String> listModelType();
}
