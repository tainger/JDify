package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.terminus.dalaran.component.processor.mapper.model.MapperConstants;
import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.console.model.DalaranConsoleConstants;
import io.terminus.dalaran.console.model.dto.BasicModelInfo;
import io.terminus.dalaran.console.model.dto.DataTemplate;
import io.terminus.dalaran.console.model.dto.ModelDTO;
import io.terminus.dalaran.console.model.query.ModelQuery;
import io.terminus.dalaran.console.repository.ModelRepository;
import io.terminus.dalaran.console.repository.ModuleRepository;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.console.service.jpa.ModelQueryService;
import io.terminus.dalaran.console.util.ExcelUtils;
import io.terminus.dalaran.core.model.BodyType;
import io.terminus.dalaran.core.model.FieldType;
import io.terminus.dalaran.core.model.ModelField;
import io.terminus.dalaran.core.model.schema.JsonSchema;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/29
 */
@Service
@Transactional
public class ModelManagementServiceImpl implements ModelManagementService {

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ModelQueryService modelQueryService;

    @Autowired
    private ModuleRepository moduleRepository;

    @Override
    public Long createModel(ModelDTO modelModel) {
        return modelRepository.save(buildEntity(modelModel)).getId();
    }

    @Override
    public void deleteModel(Long modelId) {
        modelRepository.delete(modelId);
    }

    @Override
    public ModelDTO updateModel(ModelDTO modelModel) {
        modelRepository.save(buildEntity(modelModel));
        return modelModel;
    }

    @Override
    public List<ModelDTO> queryModels(ModelQuery query) {
        List<ModelEntity> entities = modelQueryService.query(query);
        List<ModelDTO> models = new LinkedList<>();

        for (ModelEntity entity : entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Override
    public List<ModelDTO> list() {
        List<ModelEntity> entities = modelRepository.findAll();
        List<ModelDTO> models = new LinkedList<>();

        for (ModelEntity entity : entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Override
    public List<BasicModelInfo> listBasicInfoByModuleId(Long moduleId) {
        return modelQueryService.listBasicInfoByModuleId(moduleId);
    }

    @Override
    public ModelEntity getById(Long modelId) {
        return modelRepository.findOne(modelId);
    }

    @Override
    public JsonSchema importExcel(MultipartFile file, Long id) {
        try {
            Map<String, ModelField> fields = ExcelUtils.parseFirstSheet(file.getInputStream());
            JsonSchema schema = new JsonSchema();
            schema.setFields(fields);
            // TODO 这些应该扔到 service 里
            ModelEntity model = modelRepository.findOne(id);
            model.setModelSchema(JSON.toJSONString(schema));
            modelRepository.save(model);
            return schema;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonSchema();
    }

    @Override
    public Map<Long, Map<String, JsonSchema>> multiImportExcel(MultipartFile file, BodyType type) {
        try {
            Map<Long, Map<String, JsonSchema>> modelSchema = new HashMap<>();
            Map<String, Map<String, ModelField>> schemas = ExcelUtils.parseAllSheet(file.getInputStream());
            // TODO 这些应该扔到 service 里
            for (Map.Entry<String, Map<String, ModelField>> entry : schemas.entrySet()) {
                ModelEntity model = new ModelEntity();
                JsonSchema schema = new JsonSchema();
                schema.setFields(entry.getValue());
                model.setModelSchema(JSON.toJSONString(schema));
                model.setName(entry.getKey());
                model.setType(type);
                modelRepository.save(model);
                Map<String, JsonSchema> singleSchema = new HashMap<>();
                singleSchema.put(entry.getKey(), schema);
                modelSchema.put(model.getId(), singleSchema);
            }
            return modelSchema;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    @Override
    public JsonSchema importDataTemplate(DataTemplate dataTemplate, Long id) {
        Map<String, ModelField> root = new HashMap<>();
        ModelField modelField = new ModelField();
        root.put(MapperConstants.MODEL_ROOT, modelField);
        Object body = JSON.parse(dataTemplate.getDataTemplate());
        String type = body.getClass().getTypeName();
        if (isComplexType(type)) {
            buildModel(body, type, modelField);
        }
        JsonSchema schema = new JsonSchema();
        schema.setFields(root);
        ModelEntity model = modelRepository.findOne(id);
        model.setModelSchema(JSON.toJSONString(schema));
        modelRepository.save(model);
        return schema;
    }

    private void buildModel(Object body, String type, ModelField modelField) {
        Map<String, ModelField> child = new HashMap<>();
        modelField.setFields(child);
        if (type.equalsIgnoreCase(DalaranConsoleConstants.JSON_OBJECT)) {
            modelField.setType(FieldType.OBJECT);
            buildChildren(body, child);
        } else if (type.equalsIgnoreCase(DalaranConsoleConstants.JSON_ARRAY)) {
            modelField.setType(FieldType.ARRAY);
            JSONArray jsonArray = (JSONArray) body;
            if (CollectionUtils.isNotEmpty(jsonArray)) {
                Object element = jsonArray.get(0);
                String elementType = element.getClass().getTypeName();
                modelField.setSubType(getFiledType(elementType));
                if (isComplexType(elementType) && elementType.equalsIgnoreCase(DalaranConsoleConstants.JSON_OBJECT)) {
                    buildChildren(element, child);
                }
            }
        }
    }

    private void buildChildren(Object element, Map<String, ModelField> child) {
        JSONObject jsonObject = (JSONObject) element;
        jsonObject.forEach((name, value) -> {
            ModelField field = new ModelField();
            child.put(name, field);
            String fileType = value.getClass().getTypeName();
            if (!isComplexType(fileType)) {
                field.setType(getFiledType(fileType));
            }
            buildModel(value, fileType, field);
        });
    }

    private ModelEntity buildEntity(ModelDTO model) {
        ModelEntity modelEntity;
        Long id = model.getId();
        if (id == null) {
            modelEntity = new ModelEntity();
        } else {
            modelEntity = modelRepository.findOne(id);
        }
        String name = model.getName();
        if (StringUtils.isNoneBlank(name)) {
            modelEntity.setName(name);
        } else {
            modelEntity.setName("Dalaran Model");
        }
        modelEntity.setModelSchema(JSON.toJSONString(model.getModelSchema()));
        modelEntity.setType(model.getModelType());
        modelEntity.setDescription(model.getDescription());
        modelEntity.setModuleId(model.getModuleId());
        return modelEntity;
    }

    private ModelDTO buildModel(ModelEntity entity) {
        ModelDTO model = new ModelDTO();
        model.setDescription(entity.getDescription());
        model.setModuleId(entity.getModuleId());
        model.setName(entity.getName());
        model.setModelSchema(JSON.parseObject(entity.getModelSchema(), Map.class));
        model.setModelType(entity.getType());
        model.setId(entity.getId());
        return model;
    }

    private boolean isComplexType(String type) {
        switch (type) {
            case DalaranConsoleConstants.JSON_OBJECT:
            case DalaranConsoleConstants.JSON_ARRAY:
                return true;
        }
        return false;
    }

    private FieldType getFiledType(String type) {
        switch (type) {
            case DalaranConsoleConstants.JAVA_INTEGER:
                return FieldType.INTEGER;
            case DalaranConsoleConstants.JAVA_LONG:
                return FieldType.LONG;
            case DalaranConsoleConstants.JAVA_STRING:
                return FieldType.STRING;
            case DalaranConsoleConstants.JSON_OBJECT:
                return FieldType.OBJECT;
            case DalaranConsoleConstants.JSON_ARRAY:
                return FieldType.ARRAY;
        }
        return FieldType.STRING;
    }
}
