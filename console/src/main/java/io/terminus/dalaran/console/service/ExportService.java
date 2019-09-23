package io.terminus.dalaran.console.service;

import com.predic8.wsdl.Definitions;
import io.swagger.models.Swagger;
import io.terminus.dalaran.console.model.ExportData;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface ExportService {

    void importAll(MultipartFile exportData) throws IOException;

    ExportData exportAll();

    Swagger exportSwagger();

    Definitions exportWSDL();

    File exportWord();

}
