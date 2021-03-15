package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.config.*;
import io.terminus.dalaran.model.function.MappingFunctionInfo;
import io.terminus.draco.api.response.UserInfo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.List;

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

    @ApiOperation(value = "获取限流熔断器配置结构")
    @GetMapping(value = "/limiter")
    Collection<LimiterInfo> listLimiterInfo();

    @ApiOperation(value = "获取鉴权器配置结构")
    @GetMapping(value = "/authenticator")
    Collection<AuthenticatorInfo> listAuthenticatorInfo();

    @ApiOperation(value = "获取新增或更新组件所需的基本结构")
    @GetMapping(value = "/basicComponent")
    Collection<BasicComponentInfo> listBasicComponentInfo();

    @ApiOperation(value = "获取服务配置结构")
    @GetMapping(value = "/service")
    Collection<ServiceInfo> listServiceInfo();

    @ApiOperation(value = "获取可用模型配置结构")
    @GetMapping(value = "/model")
    Collection<ModelInfo> listModelInfo();

    @ApiOperation(value = "获取可用模型类型")
    @GetMapping(value = "/modelType")
    Collection<String> listModelType();

    @CrossOrigin
    @ApiOperation(value = "获取 operation 列表")
    @GetMapping(value = "/trigger/soap/operations")
    List<String> listOperations();

    @ApiOperation(value = "获取当前用户信息")
    @GetMapping(value = "/getUserInfo")
    UserInfo getUserInfo();
}
