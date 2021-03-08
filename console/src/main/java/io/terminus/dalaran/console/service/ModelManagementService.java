package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.model.ClassificationModel;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.DalaranModelTemplate;
import io.terminus.dalaran.model.dto.ModelDTO;
import io.terminus.dalaran.model.dto.basic.BasicModelInfo;
import io.terminus.dalaran.model.query.ModelQuery;
import io.terminus.dalaran.model.schema.DataTemplate;
import io.terminus.dalaran.model.schema.JsonSchema;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/28
 */
public interface ModelManagementService {

    String createModel(ModelDTO modelModel);

    void deleteModel(String modelId);

    ModelDTO updateModel(ModelDTO modelModel);

    List<ModelDTO> queryModels(ModelQuery query);

    List<ModelDTO> list();

    List<ModelDTO> listByModuleId(String moduleId);

    List<ModelDTO> listEditableModel();

    List<ModelDTO> listEditableModelByModuleId(String moduleId);

    List<BasicModelInfo> listBasicInfoByModuleId(String moduleId);

    Map<String, ClassificationModel> listClassificationModels(String moduleId);

    ModelEntity getById(String modelId);

    ModelEntity getByNameAndServiceId(String name, String serviceId);

    JsonSchema importExcel(MultipartFile file, String id);

    JsonSchema importExcelNoneId(MultipartFile file);

    Map<String, Map<String, JsonSchema>> multiImportExcel(MultipartFile file, String modelType);

    DalaranModelSchema importDataTemplate(DataTemplate dataTemplate, String id);

    DalaranModelSchema importDalaranSchema(DalaranModelSchema schema, String id);

    DalaranModelTemplate buildDataTemplate(DalaranModelSchema schema, String id);

    Map<String, String> suggestMapping(String sourceId, String targetId);

    ResponseEntity<Resource> downloadExcelTemplate();

    DalaranModelSchema importModelTemplate(DataTemplate dataTemplate, String id);

    DalaranModelSchema importDataTemplateByType(DataTemplate dataTemplate, String type);

    DalaranModelTemplate buildSwaggerDataTemplate(DalaranModelSchema schema, String type);
}