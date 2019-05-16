package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.BodyType;
import io.terminus.dalaran.console.model.dto.BasicModelInfo;
import io.terminus.dalaran.console.model.dto.ModelDTO;
import io.terminus.dalaran.console.model.query.ModelQuery;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.console.service.jpa.ModelQueryService;
import io.terminus.dalaran.console.util.ExcelUtils;
import io.terminus.dalaran.entity.manage.ModelEntity;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.repository.ModelRepository;
import io.terminus.dalaran.repository.ModuleRepository;
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
}
