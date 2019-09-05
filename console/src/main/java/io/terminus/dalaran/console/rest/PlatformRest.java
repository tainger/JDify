package io.terminus.dalaran.console.rest;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import io.terminus.common.model.Response;
import io.terminus.dalaran.console.model.*;
import io.terminus.dalaran.console.service.AuthorizeService;
import io.terminus.dalaran.console.service.ExportService;
import io.terminus.dalaran.console.service.ReleaseService;
import io.terminus.dalaran.console.service.TrantorService;
import io.terminus.dalaran.core.context.DalaranContext;
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
import java.util.Date;

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
    private Response releaseRecordList() {
        try {
            return Response.ok(releaseService.listReleaseRecordDTO());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.VERSION_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "获取某版本所有流新消息")
    @GetMapping(value = "/release/{version:.*}")
    private Response triggerFlowList(@PathVariable String version) {
        try {
            return Response.ok(releaseService.listReleasedTriggerFlowDTO(version));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.VERSION_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "发布最新的所有内容")
    @PostMapping(value = "/release")
    private Response release(@RequestBody ReleaseRequestDTO requestDTO) {
        try {
            return Response.ok(releaseService.release(requestDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.RELEASE_ERROR);
        }
    }

    @ApiOperation(value = "回滚至具体版本")
    @PostMapping(value = "/release/{version:.*}/rollback")
    private Response rollback(@PathVariable String version) {
        try {
            return Response.ok(releaseService.rollback(version));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.ROLLBACK_ERROR);
        }
    }

    @ApiOperation(value = "获取处理器配置结构")
    @GetMapping(value = "/processor")
    private Response listProcessorInfo() {
        try {
            return Response.ok(dalaranContext.getDalaranComponentContext().getAllProcessorInfo());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.PROCESSOR_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "获取触发器配置结构")
    @GetMapping(value = "/trigger")
    private Response listTriggerInfo() {
        try {
            return Response.ok(dalaranContext.getDalaranComponentContext().getAllTriggerInfo());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.TRIGGER_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "获取静态 MappingFunction 信息")
    @GetMapping(value = "/mappingFunctions")
    private Response mappingFunctions() {
        try {
            return Response.ok(dalaranContext.getDalaranFunctionContext().allFunctionInfo());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.FUNCTION_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "获取静态 MappingFunction 信息")
    @GetMapping(value = "/testMappingFunctions")
    private Response testMappingFunctions() {
        try {
            return Response.ok(dalaranContext.getDalaranFunctionContext().executeStaticFunction("ToJson", new Object[]{dalaranContext.getDalaranFunctionContext().allFunctionInfo()}));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.FUNCTION_QUERY_ERROR);
        }
    }


    @ApiOperation(value = "获取静态 MappingFunction 信息")
    @GetMapping(value = "/testMappingFunctions2")
    private Response testMappingFunctions2() {
        try {
            return Response.ok(dalaranContext.getDalaranFunctionContext().executeCustomFunction(1L, new Object[]{"myName"}));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.FUNCTION_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "获取连接器配置结构")
    @GetMapping(value = "/connector")
    private Response listConnectorInfo() {
        try {
            return Response.ok(dalaranContext.getDalaranComponentContext().getAllConnectorInfo());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.CONNECTOR_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "获取服务配置结构")
    @GetMapping(value = "/service")
    private Response listServiceInfo() {
        try {
            return Response.ok(dalaranContext.getDalaranServiceContext().getAllServiceInfo());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.SERVICE_QUERY_ERROR);
        }
    }

    @CrossOrigin
    @ApiOperation(value = "导出 Swagger 信息")
    @GetMapping(value = "/export/swagger")
    private Response exportSwagger() {
        try {
            return Response.ok(exportService.exportSwagger());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.SWAGGER_EXPORT_ERROR);
        }
    }

    @ApiOperation(value = "导出 Swagger 信息")
    @GetMapping(value = "/export/word")
    private ResponseEntity exportWord() {
        File file = exportService.exportWord();
        String currentDate = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        FileSystemResource fileResource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=dalaran-api-docs" + currentDate + ".docx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(fileResource);
    }

    @ApiOperation(value = "导出所有配置")
    @GetMapping(value = "/export")
    private ExportData exportAll(HttpServletResponse res) {
        String currentDate = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        res.addHeader("Content-Disposition", "attachment;filename=" + currentDate + ".dlr");
        return exportService.exportAll();
    }

    @ApiOperation(value = "导入所有配置, 会覆盖之前的内容")
    @PostMapping(value = "/import")
    private Response importAll(@RequestParam MultipartFile exportData) {
        ExportData importData;
        try {
            importData = JSON.parseObject(exportData.getInputStream(), ExportData.class);
        } catch (IOException e) {
            return Response.fail("配置文件读取失败, 请确认文件");
        }
        try {
            exportService.importAll(importData);
        } catch (Throwable e) {
            e.printStackTrace();
            return Response.fail("未知错误导致导入失败");
        }
        return Response.ok("导入成功");
    }

    @ApiOperation(value = "推送 Trantor 的带集成信息")
    @PostMapping(value = "/trantor")
    private Response publishTrantorIntegrationInfo(@RequestBody DalaranTrantorModule trantorModule) {
        try {
            trantorService.saveTrantorIntegrationInfo(trantorModule);
            return Response.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.TRANTOR_PUSH_ERROR);
        }
    }

    @ApiOperation(value = "获取所有 Trantor 模块信息, 以及其所有扩展点信息")
    @GetMapping(value = "/trantor")
    private Response listTrantorModule() {
        try {
            return Response.ok(trantorService.getAllModule());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.TRANTOR_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "登陆验证")
    @PostMapping(value = "/login/auth")
    private Response loginAuth(@RequestBody DalaranAccount account) {
        try {
            return Response.ok(authorizeService.authAccount(account));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.LOGIN_ERROR);
        }
    }
}
