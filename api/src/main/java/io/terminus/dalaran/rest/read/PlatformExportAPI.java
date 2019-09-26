package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.swagger.models.Swagger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/api/platform", produces = {"application/json; charset=UTF-8"})
public interface PlatformExportAPI {

    @CrossOrigin
    @ApiOperation(value = "导出 Swagger 信息")
    @GetMapping(value = "/export/swagger")
    Swagger exportSwagger();

    @ApiOperation(value = "导出 Word Rest API Docs")
    @GetMapping(value = "/export/word")
    ResponseEntity exportWord();

    @CrossOrigin
    @ApiOperation(value = "导出 WSDL 信息")
    @GetMapping(value = "/export/WSDL", produces = "text/xml;charset=UTF-8")
    String exportWSDL();
}
