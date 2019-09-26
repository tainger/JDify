package io.terminus.dalaran.console.service;

import com.predic8.wsdl.Definitions;
import io.swagger.models.Swagger;
import io.terminus.dalaran.console.ExportData;

import java.io.File;
import java.io.IOException;

public interface ExportService {

    void importAll(ExportData exportData) throws IOException;

    ExportData exportAll();

    Swagger exportSwagger();

    Definitions exportWSDL();

    File exportWord();

}
