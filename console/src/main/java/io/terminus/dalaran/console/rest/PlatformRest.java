package io.terminus.dalaran.console.rest;

import com.alibaba.fastjson.JSON;
import io.swagger.models.Swagger;
import io.terminus.dalaran.config.ConnectorInfo;
import io.terminus.dalaran.config.ProcessorInfo;
import io.terminus.dalaran.config.ServiceInfo;
import io.terminus.dalaran.config.TriggerInfo;
import io.terminus.dalaran.console.ExportData;
import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.OnException;
import io.terminus.dalaran.console.service.AuthorizeService;
import io.terminus.dalaran.console.service.ExportService;
import io.terminus.dalaran.console.service.ReleaseService;
import io.terminus.dalaran.console.service.TrantorService;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.model.DalaranAccount;
import io.terminus.dalaran.model.dto.ReleaseRecordDTO;
import io.terminus.dalaran.model.dto.ReleaseRequestDTO;
import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.model.function.MappingFunctionInfo;
import io.terminus.dalaran.rest.read.PlatformExportAPI;
import io.terminus.dalaran.rest.read.PlatformInfoAPI;
import io.terminus.dalaran.rest.read.ReleaseReadAPI;
import io.terminus.dalaran.rest.write.LoginAPI;
import io.terminus.dalaran.rest.write.PlatformImportAPI;
import io.terminus.dalaran.rest.write.ReleaseWriteAPI;
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
import java.util.Collection;
import java.util.Date;
import java.util.List;

@RestController
public class PlatformRest implements PlatformInfoAPI, PlatformImportAPI, PlatformExportAPI, ReleaseReadAPI, ReleaseWriteAPI, LoginAPI {

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

    @Override
    @OnException(message = ResponseMessage.VERSION_QUERY_ERROR)
    public List<ReleaseRecordDTO> releaseRecordList() {
        return releaseService.listReleaseRecordDTO();
    }

    @Override
    @OnException(message = ResponseMessage.VERSION_QUERY_ERROR)
    public List<TriggerFlowDTO> triggerFlowList(@PathVariable String version) {
        return releaseService.listReleasedTriggerFlowDTO(version);
    }

    @Override
    @OnException(message = ResponseMessage.RELEASE_ERROR)
    public ReleaseRecordDTO release(@RequestBody ReleaseRequestDTO requestDTO) {
        return releaseService.release(requestDTO);
    }

    @Override
    @OnException(message = ResponseMessage.ROLLBACK_ERROR)
    public ReleaseRecordDTO rollback(@PathVariable String version) {
        return releaseService.rollback(version);
    }

    @Override
    @OnException(message = ResponseMessage.PROCESSOR_QUERY_ERROR)
    public Collection<ProcessorInfo> listProcessorInfo() {
        return dalaranContext.getDalaranComponentContext().getAllProcessorInfo();
    }

    @Override
    @OnException(message = ResponseMessage.TRIGGER_QUERY_ERROR)
    public Collection<TriggerInfo> listTriggerInfo() {
        return dalaranContext.getDalaranComponentContext().getAllTriggerInfo();
    }

    @Override
    @OnException(message = ResponseMessage.FUNCTION_QUERY_ERROR)
    public Collection<MappingFunctionInfo> mappingFunctions() {
        return dalaranContext.getDalaranFunctionContext().allFunctionInfo();
    }

    @Override
    @OnException(message = ResponseMessage.CONNECTOR_QUERY_ERROR)
    public Collection<ConnectorInfo> listConnectorInfo() {
        return dalaranContext.getDalaranComponentContext().getAllConnectorInfo();
    }

    @Override
    @OnException(message = ResponseMessage.SERVICE_QUERY_ERROR)
    public Collection<ServiceInfo> listServiceInfo() {
        return dalaranContext.getDalaranServiceContext().getAllServiceInfo();
    }

    @Override
    public Collection<String> listModelType() {
        return dalaranContext.getDalaranModelTypeContext().listAllModelType();
    }

    @Override
    @CrossOrigin
    @OnException(message = ResponseMessage.SWAGGER_EXPORT_ERROR)
    public Swagger exportSwagger() {
        return exportService.exportSwagger();
    }

    @Override
    @OnException(message = ResponseMessage.SWAGGER_EXPORT_ERROR)
    public ResponseEntity exportWord() {
        File file = exportService.exportWord();
        String currentDate = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        FileSystemResource fileResource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=dalaran-api-docs" + currentDate + ".docx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(fileResource);
    }


    @Override
    @CrossOrigin
    @OnException(message = ResponseMessage.WSDL_EXPORT_ERROR)
    public String exportWSDL() {
        return exportService.exportWSDL().getAsString();
    }

    @OnException(message = ResponseMessage.CONFIG_EXPORT_ERROR)
    public ExportData exportAll(HttpServletResponse res) {
        String currentDate = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        res.addHeader("Content-Disposition", "attachment;filename=" + currentDate + ".dlr");
        return exportService.exportAll();
    }


    @Override
    @OnException(message = ResponseMessage.CONFIG_IMPORT_ERROR)
    public void importAll(@RequestParam MultipartFile importFile) {
        try {
            ExportData importData = JSON.parseObject(importFile.getInputStream(), ExportData.class);
            exportService.importAll(importData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @OnException(message = ResponseMessage.LOGIN_ERROR)
    public boolean loginAuth(@RequestBody DalaranAccount account) {
        return authorizeService.authAccount(account);
    }
}
