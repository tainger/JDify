package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.model.ClassificationModel;
import io.terminus.dalaran.model.schema.DataTemplate;
import io.terminus.dalaran.model.dto.ModelDTO;
import io.terminus.dalaran.model.dto.basic.BasicModelInfo;
import io.terminus.dalaran.model.query.ModelQuery;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.schema.ObjectSchema;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/28
 */
public interface ModelManagementService {

    Long createModel(ModelDTO modelModel);

    void deleteModel(Long modelId);

    ModelDTO updateModel(ModelDTO modelModel);

    List<ModelDTO> queryModels(ModelQuery query);

    List<ModelDTO> list();

    List<ModelDTO> listByModuleId(Long moduleId);

    List<ModelDTO> listEditableModel();

    List<ModelDTO> listEditableModelByModuleId(Long moduleId);

    List<BasicModelInfo> listBasicInfoByModuleId(Long moduleId);

    Map<String, ClassificationModel> listClassificationModels(Long moduleId);

    ModelEntity getById(Long modelId);

    ModelEntity getByNameAndServiceId(String name, String serviceId);

    JsonSchema importExcel(MultipartFile file, Long id);

    Map<Long, Map<String, JsonSchema>> multiImportExcel(MultipartFile file, String modelType);

    JsonSchema importDataTemplate(DataTemplate dataTemplate, Long id);

    ObjectSchema importDalaranSchema(ObjectSchema schema, Long id);

    String buildDataTemplate(JsonSchema schema, Long id);

    Map<String, String> suggestMapping(Long sourceId, Long targetId);

    ResponseEntity<Resource> downloadExcelTemplate();

    Object importModelTemplate(DataTemplate dataTemplate, Long id);
}