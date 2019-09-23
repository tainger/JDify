package io.terminus.dalaran.console.rest;

import com.alibaba.fastjson.JSON;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.creator.WSDLCreator;
import com.predic8.wsdl.creator.WSDLCreatorContext;
import groovy.xml.MarkupBuilder;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.Swagger;
import io.terminus.common.model.Response;
import io.terminus.dalaran.console.exception.DalaranException;
import io.terminus.dalaran.console.model.DalaranAccount;
import io.terminus.dalaran.console.model.ExportData;
import io.terminus.dalaran.console.model.ReleaseRequestDTO;
import io.terminus.dalaran.console.model.ResponseMessage;
import io.terminus.dalaran.console.model.dto.ReleaseRecordDTO;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.model.dto.trantor.TrantorModuleDTO;
import io.terminus.dalaran.console.service.AuthorizeService;
import io.terminus.dalaran.console.service.ExportService;
import io.terminus.dalaran.console.service.ReleaseService;
import io.terminus.dalaran.console.service.TrantorService;
import io.terminus.dalaran.core.config.ConnectorInfo;
import io.terminus.dalaran.core.config.ProcessorInfo;
import io.terminus.dalaran.core.config.ServiceInfo;
import io.terminus.dalaran.core.config.TriggerInfo;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.model.function.MappingFunctionInfo;
import io.terminus.dalaran.model.trantor.DalaranTrantorModule;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api/platform", produces = {"application/json; charset=UTF-8"})
public class PlatformRest {

    @Autowired
    private DalaranContext dalaranContext;

    @Autowired
    private ReleaseService releaseService;

    @Autowired
    private TrantorService trantorService;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private ExportService exportService;

    @ApiOperation(value = "获取某版本所有流新消息")
    @GetMapping(value = "/release")
    @DalaranException(value = ResponseMessage.VERSION_QUERY_ERROR)
    public List<ReleaseRecordDTO> releaseRecordList() {
        return releaseService.listReleaseRecordDTO();
    }

    @ApiOperation(value = "获取某版本所有流新消息")
    @GetMapping(value = "/release/{version:.*}")
    @DalaranException(value = ResponseMessage.VERSION_QUERY_ERROR)
    public List<TriggerFlowDTO> triggerFlowList(@PathVariable String version) {
        return releaseService.listReleasedTriggerFlowDTO(version);
    }

    @ApiOperation(value = "发布最新的所有内容")
    @PostMapping(value = "/release")
    @DalaranException(value = ResponseMessage.RELEASE_ERROR)
    public ReleaseRecordDTO release(@RequestBody ReleaseRequestDTO requestDTO) {
        return releaseService.release(requestDTO);
    }

    @ApiOperation(value = "回滚至具体版本")
    @PostMapping(value = "/release/{version:.*}/rollback")
    @DalaranException(value = ResponseMessage.ROLLBACK_ERROR)
    public ReleaseRecordDTO rollback(@PathVariable String version) {
        return releaseService.rollback(version);
    }

    @ApiOperation(value = "获取处理器配置结构")
    @GetMapping(value = "/processor")
    @DalaranException(value = ResponseMessage.PROCESSOR_QUERY_ERROR)
    public Collection<ProcessorInfo> listProcessorInfo() {
        return dalaranContext.getDalaranComponentContext().getAllProcessorInfo();
    }

    @ApiOperation(value = "获取触发器配置结构")
    @GetMapping(value = "/trigger")
    @DalaranException(value = ResponseMessage.TRIGGER_QUERY_ERROR)
    public Collection<TriggerInfo> listTriggerInfo() {
        return dalaranContext.getDalaranComponentContext().getAllTriggerInfo();
    }

    @ApiOperation(value = "获取静态 MappingFunction 信息")
    @GetMapping(value = "/mappingFunctions")
    @DalaranException(value = ResponseMessage.FUNCTION_QUERY_ERROR)
    public Collection<MappingFunctionInfo> mappingFunctions() {
        return dalaranContext.getDalaranFunctionContext().allFunctionInfo();
    }

    @ApiOperation(value = "获取静态 MappingFunction 信息")
    @GetMapping(value = "/testMappingFunctions")
    @DalaranException(value = ResponseMessage.FUNCTION_QUERY_ERROR)
    public Object testMappingFunctions() {
        return dalaranContext.getDalaranFunctionContext().executeStaticFunction("ToJson", new Object[]{dalaranContext.getDalaranFunctionContext().allFunctionInfo()});
    }

    @ApiOperation(value = "获取静态 MappingFunction 信息")
    @GetMapping(value = "/testMappingFunctions2")
    @DalaranException(value = ResponseMessage.FUNCTION_QUERY_ERROR)
    public Object testMappingFunctions2() {
        return dalaranContext.getDalaranFunctionContext().executeCustomFunction(1L, new Object[]{"myName"});
    }

    @ApiOperation(value = "获取连接器配置结构")
    @GetMapping(value = "/connector")
    @DalaranException(value = ResponseMessage.CONNECTOR_QUERY_ERROR)
    public Collection<ConnectorInfo> listConnectorInfo() {
        return dalaranContext.getDalaranComponentContext().getAllConnectorInfo();
    }

    @ApiOperation(value = "获取服务配置结构")
    @GetMapping(value = "/service")
    @DalaranException(value = ResponseMessage.SERVICE_QUERY_ERROR)
    public Collection<ServiceInfo> listServiceInfo() {
        return dalaranContext.getDalaranServiceContext().getAllServiceInfo();
    }

    @CrossOrigin
    @ApiOperation(value = "导出 Swagger 信息")
    @GetMapping(value = "/export/swagger")
    @DalaranException(value = ResponseMessage.SWAGGER_EXPORT_ERROR)
    public Swagger exportSwagger() {
        return exportService.exportSwagger();
    }

    @ApiOperation(value = "导出 Swagger 信息")
    @GetMapping(value = "/export/word")
    @DalaranException(value = ResponseMessage.SWAGGER_EXPORT_ERROR)
    public ResponseEntity exportWord() {
        File file = exportService.exportWord();
        String currentDate = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        FileSystemResource fileResource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=dalaran-api-docs" + currentDate + ".docx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(fileResource);
    }

    @CrossOrigin
    @ApiOperation(value = "导出 WSDL 信息")
    @GetMapping(value = "/export/WSDL", produces = "text/xml;charset=UTF-8")
    @DalaranException(value = ResponseMessage.WSDL_EXPORT_ERROR)
    public String exportWSDL() {
        return exportService.exportWSDL().getAsString();
    }

    @ApiOperation(value = "导出所有配置")
    @GetMapping(value = "/export")
    @DalaranException(value = ResponseMessage.CONFIG_EXPORT_ERROR)
    public ExportData exportAll(HttpServletResponse res) {
        String currentDate = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        res.addHeader("Content-Disposition", "attachment;filename=" + currentDate + ".dlr");
        return exportService.exportAll();
    }

    @ApiOperation(value = "导入所有配置, 会覆盖之前的内容")
    @PostMapping(value = "/import")
    @DalaranException(value = ResponseMessage.CONFIG_IMPORT_ERROR)
    public void importAll(@RequestParam MultipartFile importFile) throws Exception {
        ExportData importData = JSON.parseObject(importFile.getInputStream(), ExportData.class);
        exportService.importAll(importData);
    }

    @ApiOperation(value = "推送 Trantor 的带集成信息")
    @PostMapping(value = "/trantor")
    @DalaranException(value = ResponseMessage.TRANTOR_PUSH_ERROR)
    public void publishTrantorIntegrationInfo(@RequestBody DalaranTrantorModule trantorModule) {
        trantorService.saveTrantorIntegrationInfo(trantorModule);
    }

    @ApiOperation(value = "获取所有 Trantor 模块信息, 以及其所有扩展点信息")
    @GetMapping(value = "/trantor")
    @DalaranException(value = ResponseMessage.TRANTOR_QUERY_ERROR)
    public List<TrantorModuleDTO> listTrantorModule() {
        return trantorService.getAllModule();
    }

    @ApiOperation(value = "登陆验证")
    @PostMapping(value = "/login/auth")
    @DalaranException(value = ResponseMessage.LOGIN_ERROR)
    public Response loginAuth(@RequestBody DalaranAccount account) {
        return Response.ok(authorizeService.authAccount(account));
    }
}
