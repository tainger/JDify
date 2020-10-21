package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.OnException;
import io.terminus.dalaran.console.repository.ModelRepository;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.model.ClassificationModel;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.DalaranModelTemplate;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.dto.ModelDTO;
import io.terminus.dalaran.model.query.ModelQuery;
import io.terminus.dalaran.model.schema.DataTemplate;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.rest.read.ModelReadAPI;
import io.terminus.dalaran.rest.write.ModelImportAPI;
import io.terminus.dalaran.rest.write.ModelWriteAPI;
import org.hibernate.validator.parameternameprovider.ReflectionParameterNameProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ModelManagementRest implements ModelReadAPI, ModelWriteAPI, ModelImportAPI {

    @Autowired
    private ModelManagementService modelManagementService;

    @Autowired
    private ModelRepository modelRepository;

    @Override
    @OnException(code = ResponseMessage.MODEL_QUERY_ERROR)
    public List<ModelDTO> query(ModelQuery query) {
        return modelManagementService.queryModels(query);
    }

    @Override
    @OnException(code = ResponseMessage.MODEL_CREATE_ERROR)
    public Long create(@RequestBody ModelDTO model) {
        return modelManagementService.createModel(model);
    }

    @Override
    @OnException(code = ResponseMessage.MODEL_UPDATE_ERROR)
    public ModelDTO update(@RequestBody ModelDTO model) {
        return modelManagementService.updateModel(model);
    }

    @Override
    @OnException(code = ResponseMessage.MODEL_DELETE_ERROR)
    public void deleteById(@RequestParam Long id) {
        modelManagementService.deleteModel(id);
    }

    @Override
    @OnException(code = ResponseMessage.MODEL_QUERY_ERROR)
    public List<ModelDTO> list() {
        return modelManagementService.list();
    }

    @Override
    @OnException(code = ResponseMessage.MODEL_QUERY_ERROR)
    public List<ModelDTO> listByModuleId(@PathVariable Long moduleId) {
        return modelManagementService.listByModuleId(moduleId);
    }

    @Override
    @OnException(code = ResponseMessage.MODEL_QUERY_ERROR)
    public List<ModelDTO> listEditable() {
        return modelManagementService.listEditableModel();
    }

    @Override
    @OnException(code = ResponseMessage.MODEL_QUERY_ERROR)
    public List<ModelDTO> listEditableByModuleId(@PathVariable Long moduleId) {
        return modelManagementService.listEditableModelByModuleId(moduleId);
    }

    @Override
    @OnException(code = ResponseMessage.MODEL_QUERY_ERROR)
    public Map<String, ClassificationModel> listClassificationByModuleId(@PathVariable Long moduleId) {
        return modelManagementService.listClassificationModels(moduleId);
    }

    @Override
    @OnException(code = ResponseMessage.BUILD_MAPPING_SUGGEST_ERROR)
    public Map<String, String> suggestMapping(@RequestParam Long sourceId, @RequestParam Long targetId) {
        return modelManagementService.suggestMapping(sourceId, targetId);
    }

    @Override
    @OnException(code = ResponseMessage.EXCEL_PARSE_ERROR)
    public JsonSchema importExcel(@RequestParam MultipartFile file, @PathVariable long id) {
        return modelManagementService.importExcel(file, id);
    }

    @Override
    @OnException(code = ResponseMessage.EXCEL_PARSE_ERROR)
    public JsonSchema importExcelNoneId(@RequestParam MultipartFile file) {
        return modelManagementService.importExcelNoneId(file);
    }

    @Override
    @OnException(code = ResponseMessage.DATA_TEMPLATE_PARSE_ERROR)
    public DalaranModelSchema importDataTemplate(@RequestBody DataTemplate dataTemplate, @PathVariable long id) {
        return modelManagementService.importModelTemplate(dataTemplate, id);
    }

    @Override
    @OnException(code = ResponseMessage.DATA_TEMPLATE_PARSE_ERROR)
    public DalaranModelSchema importDataTemplateByType(@RequestBody DataTemplate dataTemplate, @PathVariable String type){
        return modelManagementService.importDataTemplateByType(dataTemplate, type);
    }

    @Override
    @OnException(code = ResponseMessage.DATA_TEMPLATE_PARSE_ERROR)
    public DalaranModelSchema importDalaranSchema(@RequestBody DalaranModelSchema objectSchema, @PathVariable long id) {
        return modelManagementService.importDalaranSchema(objectSchema, id);
    }

    @Override
    @OnException(code = ResponseMessage.MODEL_EXAMPLE_BUILD_ERROR)
    public DalaranModelTemplate buildRequestTemplate(@RequestBody DalaranModelSchema schema, @PathVariable long id) {
        return modelManagementService.buildDataTemplate(schema, id);
    }

    // TODO 待开发
    @Override
    public Map<String, ModelField> importCodeTemplate(@RequestBody String codeTemplate, @PathVariable long id) {
        return new HashMap<>();
    }

    // TODO 其实意义不大
    @Override
    @OnException(code = ResponseMessage.EXCEL_PARSE_ERROR)
    public Map<Long, Map<String, JsonSchema>> multiImportExcel(@RequestParam MultipartFile file, @RequestParam String modelType) {
        return modelManagementService.multiImportExcel(file, modelType);
    }

    @Override
    @OnException(code = ResponseMessage.TEMPLATE_DOWNLOAD_ERROR)
    public ResponseEntity<Resource> downloadExcelTemplate() {
        return modelManagementService.downloadExcelTemplate();
    }
}
