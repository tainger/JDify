package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.console.model.DalaranAccount;
import io.terminus.dalaran.console.model.ReleaseRequestDTO;
import io.terminus.dalaran.console.model.dto.ReleaseRecordDTO;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.model.dto.trantor.TrantorModuleDTO;
import io.terminus.dalaran.console.service.AuthorizeService;
import io.terminus.dalaran.console.service.ReleaseService;
import io.terminus.dalaran.console.service.TrantorService;
import io.terminus.dalaran.core.config.ConnectorInfo;
import io.terminus.dalaran.core.config.ProcessorInfo;
import io.terminus.dalaran.core.config.ServiceInfo;
import io.terminus.dalaran.core.config.TriggerInfo;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.model.function.MappingFunctionInfo;
import io.terminus.dalaran.model.trantor.DalaranTrantorModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/platform")
public class PlatformRest {

    @Autowired
    private DalaranContext dalaranContext;

    @Autowired
    private ReleaseService releaseService;

    @Autowired
    private TrantorService trantorService;

    @Autowired
    private AuthorizeService authorizeService;

    @ApiOperation(value = "获取某版本所有流新消息")
    @GetMapping(value = "/release")
    private List<ReleaseRecordDTO> releaseRecordList() {
        return releaseService.listReleaseRecordDTO();
    }

    @ApiOperation(value = "获取某版本所有流新消息")
    @GetMapping(value = "/release/{version:.*}")
    private List<TriggerFlowDTO> triggerFlowList(@PathVariable String version) {
        return releaseService.listReleasedTriggerFlowDTO(version);
    }

    @ApiOperation(value = "发布最新的所有内容")
    @PostMapping(value = "/release")
    private ReleaseRecordDTO release(@RequestBody ReleaseRequestDTO requestDTO) {
        return releaseService.release(requestDTO);
    }

    @ApiOperation(value = "回滚至具体版本")
    @PostMapping(value = "/release/{version:.*}/rollback")
    private ReleaseRecordDTO rollback(@PathVariable String version) {
        return releaseService.rollback(version);
    }

    @ApiOperation(value = "获取处理器配置结构")
    @GetMapping(value = "/processor")
    private Collection<ProcessorInfo> listProcessorInfo() {
        return dalaranContext.getDalaranComponentContext().getAllProcessorInfo();
    }

    @ApiOperation(value = "获取触发器配置结构")
    @GetMapping(value = "/trigger")
    private Collection<TriggerInfo> listTriggerInfo() {
        return dalaranContext.getDalaranComponentContext().getAllTriggerInfo();
    }

    @ApiOperation(value = "获取静态 MappingFunction 信息")
    @GetMapping(value = "/mappingFunctions")
    private Collection<MappingFunctionInfo> mappingFunctions() {
        return dalaranContext.getDalaranFunctionContext().allFunctionInfo();
    }

    @ApiOperation(value = "获取静态 MappingFunction 信息")
    @GetMapping(value = "/testMappingFunctions")
    private Object testMappingFunctions() {
        return dalaranContext.getDalaranFunctionContext().executeStaticFunction("ToJson", new Object[]{dalaranContext.getDalaranFunctionContext().allFunctionInfo()});
    }


    @ApiOperation(value = "获取静态 MappingFunction 信息")
    @GetMapping(value = "/testMappingFunctions2")
    private Object testMappingFunctions2() {
        return dalaranContext.getDalaranFunctionContext().executeCustomFunction(1L, new Object[]{"myName"});
    }

    @ApiOperation(value = "获取连接器配置结构")
    @GetMapping(value = "/connector")
    private Collection<ConnectorInfo> listConnectorInfo() {
        return dalaranContext.getDalaranComponentContext().getAllConnectorInfo();
    }

    @ApiOperation(value = "获取服务配置结构")
    @GetMapping(value = "/service")
    private Collection<ServiceInfo> listServiceInfo() {
        return dalaranContext.getDalaranServiceContext().getAllServiceInfo();
    }

    @ApiOperation(value = "推送 Trantor 的带集成信息")
    @PostMapping(value = "/trantor")
    private void publishTrantorIntegrationInfo(@RequestBody DalaranTrantorModule trantorModule) {
        trantorService.saveTrantorIntegrationInfo(trantorModule);
    }

    @ApiOperation(value = "获取所有 Trantor 模块信息, 以及其所有扩展点信息")
    @GetMapping(value = "/trantor")
    private List<TrantorModuleDTO> listTrantorModule() {
        return trantorService.getAllModule();
    }

    @ApiOperation(value = "登陆验证")
    @PostMapping(value = "/login/auth")
    private boolean loginAuth(@RequestBody DalaranAccount account) {
        return authorizeService.authAccount(account);
    }

    @ApiOperation(value = "登陆")
    @GetMapping(value = "/login")
    private String login() {
        return "login";
    }
}
