package io.terminus.dalaran.api.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.models.Swagger;
import io.terminus.dalaran.config.ConnectorInfo;
import io.terminus.dalaran.config.ProcessorInfo;
import io.terminus.dalaran.config.ServiceInfo;
import io.terminus.dalaran.config.TriggerInfo;
import io.terminus.dalaran.model.DalaranAccount;
import io.terminus.dalaran.model.dto.ReleaseRecordDTO;
import io.terminus.dalaran.model.dto.ReleaseRequestDTO;
import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.model.function.MappingFunctionInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

@RequestMapping(value = "/api/platform", produces = {"application/json; charset=UTF-8"})
public interface PlatformRestAPI {

    @ApiOperation(value = "获取某版本所有流新消息")
    @GetMapping(value = "/release")
    List<ReleaseRecordDTO> releaseRecordList();

    @ApiOperation(value = "获取某版本所有流新消息")
    @GetMapping(value = "/release/{version:.*}")
    List<TriggerFlowDTO> triggerFlowList(@PathVariable String version);

    @ApiOperation(value = "发布最新的所有内容")
    @PostMapping(value = "/release")
    ReleaseRecordDTO release(@RequestBody ReleaseRequestDTO requestDTO);

    @ApiOperation(value = "回滚至具体版本")
    @PostMapping(value = "/release/{version:.*}/rollback")
    ReleaseRecordDTO rollback(@PathVariable String version);

    @ApiOperation(value = "获取处理器配置结构")
    @GetMapping(value = "/processor")
    Collection<ProcessorInfo> listProcessorInfo();

    @ApiOperation(value = "获取触发器配置结构")
    @GetMapping(value = "/trigger")
    Collection<TriggerInfo> listTriggerInfo();

    @ApiOperation(value = "获取静态 MappingFunction 信息")
    @GetMapping(value = "/mappingFunctions")
    Collection<MappingFunctionInfo> mappingFunctions();

    @ApiOperation(value = "获取静态 MappingFunction 信息")
    @GetMapping(value = "/testMappingFunctions")
    Object testMappingFunctions();

    @ApiOperation(value = "获取静态 MappingFunction 信息")
    @GetMapping(value = "/testMappingFunctions2")
    Object testMappingFunctions2();

    @ApiOperation(value = "获取连接器配置结构")
    @GetMapping(value = "/connector")
    Collection<ConnectorInfo> listConnectorInfo();

    @ApiOperation(value = "获取服务配置结构")
    @GetMapping(value = "/service")
    Collection<ServiceInfo> listServiceInfo();

    @CrossOrigin
    @ApiOperation(value = "导出 Swagger 信息")
    @GetMapping(value = "/export/swagger")
    Swagger exportSwagger();

    @ApiOperation(value = "导出 Swagger 信息")
    @GetMapping(value = "/export/word")
    ResponseEntity exportWord();

    @CrossOrigin
    @ApiOperation(value = "导出 WSDL 信息")
    @GetMapping(value = "/export/WSDL", produces = "text/xml;charset=UTF-8")
    String exportWSDL();

    @ApiOperation(value = "导入所有配置, 会覆盖之前的内容")
    @PostMapping(value = "/import")
    void importAll(@RequestParam MultipartFile importFile);

    @ApiOperation(value = "登陆验证")
    @PostMapping(value = "/login/auth")
    boolean loginAuth(@RequestBody DalaranAccount account);
}
