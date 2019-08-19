package io.terminus.dalaran.console.service;

import io.terminus.dalaran.component.processor.mapper.model.SimpleMapping;
import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.console.model.dto.DataTemplate;
import io.terminus.dalaran.console.model.dto.ModelDTO;
import io.terminus.dalaran.console.model.dto.basic.BasicModelInfo;
import io.terminus.dalaran.console.model.query.ModelQuery;
import io.terminus.dalaran.model.BodyType;
import io.terminus.dalaran.model.schema.JsonSchema;
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

    List<ModelDTO> listNoHidden();

    List<BasicModelInfo> listBasicInfoByModuleId(Long moduleId);

    ModelEntity getById(Long modelId);

    ModelEntity getByNameAndServiceId(String name, Long serviceId);

    JsonSchema importExcel(MultipartFile file, Long id);

    Map<Long, Map<String, JsonSchema>> multiImportExcel(MultipartFile file, BodyType type);

    JsonSchema importDataTemplate(DataTemplate dataTemplate, Long id);

    String buildDataTemplate(JsonSchema schema, Long id);

    Map<String, SimpleMapping> suggestMapping(Long sourceId, Long targetId);
}