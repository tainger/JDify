package io.terminus.dalaran.console.service;

import com.predic8.wsdl.Definitions;
import io.swagger.models.Swagger;
import io.terminus.dalaran.console.model.ExportData;

import java.io.File;

public interface ExportService {

    void importAll(ExportData exportData);

    ExportData exportAll();

    Swagger exportSwagger();

    Definitions exportWSDL();

    File exportWord();

}
