package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.swagger.models.Swagger;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RequestMapping(value = "/api/platform", produces = {"application/json; charset=UTF-8"})
public interface PlatformExportAPI {

    @CrossOrigin
    @ApiOperation(value = "根据触发器类型导出 ApiDocs")
    @GetMapping(value = "/export/api-docs/{triggerType}")
    Object exportApiDocs(@PathVariable String triggerType);

    @CrossOrigin
    @ApiOperation(value = "根据触发器类型导出 Word 格式接口文档 Docs")
    @GetMapping(value = "/export/word-docs/{triggerType}")
    ResponseEntity exportWordDocs(@PathVariable String triggerType);

    @CrossOrigin
    @ApiOperation(value = "导出 Swagger 信息")
    @GetMapping(value = "/export/swagger")
    Swagger exportSwagger();

    @CrossOrigin
    @ApiOperation(value = "导出 Word Rest API Docs")
    @GetMapping(value = "/export/word")
    ResponseEntity exportWord();

    @CrossOrigin
    @ApiOperation(value = "导出 WSDL 信息")
    @GetMapping(value = "/export/WSDL", produces = "text/xml;charset=UTF-8")
    String exportWSDL();

    @CrossOrigin
    @ApiOperation(value = "导出 单个operation的WSDL 信息")
    @GetMapping(value = "/export/WSDL/{operation}", produces = "text/xml;charset=UTF-8")
    String exportOperationWSDL(@PathVariable String operation);

//    @CrossOrigin
//    @ApiOperation(value = "批量导出流程")
//    @GetMapping(value = "/export/flow")
//    ExportData exportFlow(@PathVariable("ids") String ids);
}
