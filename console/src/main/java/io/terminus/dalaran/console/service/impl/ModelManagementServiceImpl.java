package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import io.terminus.dalaran.DalaranConsoleConstants;
import io.terminus.dalaran.ServiceType;
import io.terminus.dalaran.component.processor.mapper.model.MapperConstants;
import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.console.entity.ServiceEntity;
import io.terminus.dalaran.console.repository.ModelRepository;
import io.terminus.dalaran.console.repository.ServiceRepository;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.console.service.jpa.ModelQueryService;
import io.terminus.dalaran.console.util.ExcelUtils;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.resource.repository.ModuleRepository;
import io.terminus.dalaran.model.*;
import io.terminus.dalaran.model.dto.ModelDTO;
import io.terminus.dalaran.model.dto.basic.BasicModelInfo;
import io.terminus.dalaran.model.query.ModelQuery;
import io.terminus.dalaran.model.schema.DataTemplate;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.schema.ObjectSchema;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private DalaranContext dalaranContext;

    private static final String COMMON_MODEL = "common";

    private JaroWinklerDistance jd = new JaroWinklerDistance();

    @Override
    public Long createModel(ModelDTO modelModel) {
        return modelRepository.save(buildEntity(modelModel)).getId();
    }

    @Override
    public void deleteModel(Long modelId) {
        modelRepository.deleteById(modelId);
    }

    @Override
    public ModelDTO updateModel(ModelDTO modelModel) {
        modelRepository.save(buildEntity(modelModel));
        return modelModel;
    }

    @Override
    public List<ModelDTO> queryModels(ModelQuery query) {
        List<ModelEntity> entities = modelQueryService.query(query);
        return entities.stream().map(this::buildModel).collect(Collectors.toList());
    }

    @Override
    public List<ModelDTO> list() {
        List<ModelEntity> entities = modelRepository.findAll();
        return entities.stream().map(this::buildModel).collect(Collectors.toList());
    }

    @Override
    public List<ModelDTO> listByModuleId(Long moduleId) {
        List<ModelEntity> entities = modelRepository.findByModuleId(moduleId);
        return entities.stream().map(this::buildModel).collect(Collectors.toList());
    }

    @Override
    public List<ModelDTO> listEditableModel() {
        List<ModelEntity> entities = modelRepository.findByTargetTypeIn(ModelTargetType.editableTypes());
        return entities.stream().map(this::buildModel).collect(Collectors.toList());
    }

    @Override
    public List<ModelDTO> listEditableModelByModuleId(Long moduleId) {
        List<ModelEntity> entities = modelRepository.findByTargetTypeInAndModuleId(ModelTargetType.editableTypes(), moduleId);
        return entities.stream().map(this::buildModel).collect(Collectors.toList());
    }

    @Override
    public List<BasicModelInfo> listBasicInfoByModuleId(Long moduleId) {
        return modelQueryService.listBasicInfoByModuleId(moduleId);
    }

    @Override
    public Map<String, ClassificationModel> listClassificationModels(Long moduleId) {
        List<ModelEntity> entities = modelRepository.findByModuleId(moduleId);
        List<ModelDTO> models = entities.stream().map(this::buildModel).collect(Collectors.toList());
        return buildClassificationModel(models);
    }

    @Override
    public ModelEntity getById(Long modelId) {
        return modelRepository.findById(modelId).get();
    }

    @Override
    public ModelEntity getByNameAndServiceId(String name, String serviceId) {
        return modelRepository.findByNameAndTargetTypeAndTargetId(name, ModelTargetType.Service, serviceId);
    }

    @Override
    public JsonSchema importExcel(MultipartFile file, Long id) {
        try {
            Map<String, ModelField> fields = ExcelUtils.parseFirstSheet(file.getInputStream());
            JsonSchema schema = new JsonSchema();
            schema.setFields(fields);
            ModelEntity model = modelRepository.findById(id).get();
            model.setModelSchema(JSON.toJSONString(schema));
            modelRepository.save(model);
            return schema;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonSchema();
    }

    @Override
    public Map<Long, Map<String, JsonSchema>> multiImportExcel(MultipartFile file, String modelType) {
        try {
            Map<Long, Map<String, JsonSchema>> modelSchema = new HashMap<>();
            Map<String, Map<String, ModelField>> schemas = ExcelUtils.parseAllSheet(file.getInputStream());
            for (Map.Entry<String, Map<String, ModelField>> entry : schemas.entrySet()) {
                ModelEntity model = new ModelEntity();
                JsonSchema schema = new JsonSchema();
                schema.setFields(entry.getValue());
                model.setModelSchema(JSON.toJSONString(schema));
                model.setName(entry.getKey());
                model.setType(modelType);
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
        SortedMap<String, ModelField> root = new TreeMap<>();
        ModelField modelField = new ModelField();
        root.put(MapperConstants.MODEL_ROOT, modelField);
        Object body = JSON.parse(dataTemplate.getDataTemplate());
        String type = body.getClass().getTypeName();
        if (isComplexType(type)) {
            buildModel(body, type, modelField);
        }
        JsonSchema schema = new JsonSchema();
        schema.setFields(root);
        ModelEntity model = modelRepository.findById(id).get();
        model.setModelSchema(JSON.toJSONString(schema));
        modelRepository.save(model);
        return schema;
    }

    @Override
    public Object importModelTemplate(DataTemplate dataTemplate, Long id) {
        ModelEntity model = modelRepository.findById(id).get();
        DalaranModelSchema schema = dalaranContext.getDalaranModelTypeContext().getModelType(model.getType()).importTemplateData(dataTemplate);
        model.setModelSchema(JSON.toJSONString(schema));
        modelRepository.save(model);
        return schema;
    }

    @Override
    public ObjectSchema importDalaranSchema(ObjectSchema schema, Long id) {
        ModelEntity model = modelRepository.findById(id).get();
        model.setModelSchema(JSON.toJSONString(schema));
        modelRepository.save(model);
        return schema;
    }

    @Override
    public String buildDataTemplate(DalaranModelSchema schema, Long id) {
        Map<String, ModelField> modelField = schema.getFields();
        ModelField root = modelField.get(DalaranConsoleConstants.MODEL_FIELD_ROOT);
        Object body = buildTemplateBody(root, "");
        if (body != null) {
            return JSON.toJSONString(body);
        }
        return null;
    }

    @Override
    public Map<String, String> suggestMapping(Long sourceId, Long targetId) {
        ModelEntity sourceEntity = modelRepository.findById(sourceId).get();
        Class<? extends DalaranModelSchema> sourceSchemaType = dalaranContext.getDalaranModelTypeContext().getModelSchema(sourceEntity.getType());
        DalaranModelSchema sourceModelSchema = JSON.parseObject(sourceEntity.getModelSchema(), sourceSchemaType);

        ModelEntity targetEntity = modelRepository.findById(targetId).get();
        Class<? extends DalaranModelSchema> targetSchemaType = dalaranContext.getDalaranModelTypeContext().getModelSchema(targetEntity.getType());
        DalaranModelSchema targetModelSchema = JSON.parseObject(targetEntity.getModelSchema(), targetSchemaType);
        Map<String, String> mappings = new HashMap<>();
        deepBuildSuggest(sourceModelSchema.getFields(), targetModelSchema.getFields(), new ArrayList<>(), new ArrayList<>(), mappings);
        return mappings;
    }

    @Override
    public ResponseEntity<Resource> downloadExcelTemplate() {
        Resource resource = new ClassPathResource(DalaranConsoleConstants.MODEL_EXCEL_TEMPLATE);
        try {
            InputStream inputStream = resource.getInputStream();
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            return ResponseEntity.ok().contentLength(resource.contentLength())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(inputStreamResource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void deepBuildSuggest(Map<String, ModelField> sourceFields, Map<String, ModelField> targetFields,
                                  List<String> sourceParentPath, List<String> targetParentPath, Map<String, String> mappings) {
        if (sourceFields == null || targetFields == null) {
            return;
        }
        for (Map.Entry<String, ModelField> targetEntry : targetFields.entrySet()) {
            double maxJD = 0;
            Map.Entry<String, ModelField> suggestField = null;
            for (Map.Entry<String, ModelField> sourceEntry : sourceFields.entrySet()) {
                // TODO 这里可以处理一下 驼峰转换之类的, 增加建议映射准确度
                double currentJD = jd.apply(targetEntry.getKey().toLowerCase(), sourceEntry.getKey().toLowerCase());
                if (maxJD < currentJD) {
                    maxJD = currentJD;
                    suggestField = sourceEntry;
                }
            }
            // 如果小于 0.5 相似性就很差了
            if (suggestField == null || maxJD <= 0.5) {
                continue;
            }
            if (targetEntry.getValue().getType().isBasicType() && suggestField.getValue().getType().isBasicType()) {
                List<String> newSourceParentPath = new ArrayList<>(sourceParentPath);
                List<String> newTargetParentPath = new ArrayList<>(targetParentPath);
                newSourceParentPath.add(suggestField.getKey());
                newTargetParentPath.add(targetEntry.getKey());

                mappings.put(StringUtils.join(newTargetParentPath, "."), StringUtils.join(newSourceParentPath, "."));
            } else if (targetEntry.getValue().getType() == suggestField.getValue().getType()) {
                List<String> newSourceParentPath = new ArrayList<>(sourceParentPath);
                List<String> newTargetParentPath = new ArrayList<>(targetParentPath);
                newSourceParentPath.add(suggestField.getKey());
                newTargetParentPath.add(targetEntry.getKey());
                deepBuildSuggest(suggestField.getValue().getFields(), targetEntry.getValue().getFields(),
                        newSourceParentPath, newTargetParentPath, mappings);
            }
        }

    }

    private Object buildTemplateBody(ModelField parent, String parentFieldName) {
        if (parent == null) {
            return Maps.newHashMap();
        }
        FieldType parentType = parent.getType();
        if (parentType == FieldType.OBJECT) {
            return handleChildBody(parent);
        } else if (parentType == FieldType.ARRAY) {
            FieldType subType = parent.getSubType();
            if (subType == FieldType.OBJECT) {
                List<Object> list = new ArrayList<>();
                for (int i = 0; i < DalaranConsoleConstants.MODEL_ARRAY_SIZE; i++) {
                    list.add(handleChildBody(parent));
                }
                return list;
            } else {
                List<Object> list = new ArrayList<>();
                for (int i = 0; i < DalaranConsoleConstants.MODEL_ARRAY_SIZE; i++) {
                    list.add(getBasicValue(subType, parentFieldName, i));
                }
                return list;
            }
        } else {
            return getBasicValue(parentType, parentFieldName, 0);
        }
    }

    private Object handleChildBody(ModelField parentField) {
        Map<String, Object> request = new HashMap<>();
        Map<String, ModelField> child = parentField.getFields();
        if (child != null) {
            child.forEach((name, field) -> {
                Object value = buildTemplateBody(field, name);
                request.put(name, value);
            });
        }
        return request;
    }

    private Object getBasicValue(FieldType type, String fieldName, int index) {
        if (type != null) {
            switch (type) {
                case STRING:
                    return fieldName + index;
                case INTEGER:
                    return index;
                case BOOLEAN:
                    return true;
                case DATE:
                    return new Date();
                case FLOAT:
                    return (float) index + 1.1;
            }
        }
        return "";
    }

    private void buildModel(Object body, String type, ModelField modelField) {
        SortedMap<String, ModelField> child = new TreeMap<>();
        modelField.setFields(child);
        if (type.equalsIgnoreCase(DalaranConsoleConstants.JSON_OBJECT)) {
            modelField.setType(FieldType.OBJECT);
            buildChildren(body, child);
        } else if (type.equalsIgnoreCase(DalaranConsoleConstants.JSON_ARRAY)) {
            modelField.setType(FieldType.ARRAY);
            JSONArray jsonArray = (JSONArray) body;
            if (CollectionUtils.isNotEmpty(jsonArray)) {
                jsonArray.forEach(element -> {
                    String elementType = element.getClass().getTypeName();
                    modelField.setSubType(getFiledType(elementType));
                    if (isComplexType(elementType) && elementType.equalsIgnoreCase(DalaranConsoleConstants.JSON_OBJECT)) {
                        buildChildren(element, child);
                    }
                });
            }
        }
    }

    private void buildChildren(Object element, SortedMap<String, ModelField> child) {
        JSONObject jsonObject = (JSONObject) element;
        jsonObject.forEach((name, value) -> {
            ModelField field = new ModelField();
            child.put(name, field);
            if (value != null) {
                String fileType = value.getClass().getTypeName();
                if (!isComplexType(fileType)) {
                    field.setType(getFiledType(fileType));
                } else {
                    buildModel(value, fileType, field);
                }
            }
        });
    }

    private ModelEntity buildEntity(ModelDTO model) {
        ModelEntity modelEntity;
        Long id = model.getId();
        if (id == null) {
            modelEntity = new ModelEntity();
        } else {
            modelEntity = modelRepository.findById(id).get();
        }
        String name = model.getName();
        if (StringUtils.isNoneBlank(name)) {
            modelEntity.setName(name);
        } else {
            modelEntity.setName("Dalaran Model");
        }

        Map<String, Object> modelSchema = model.getModelSchema();
//        String modelType = model.getModelType();
//        if (modelType.equalsIgnoreCase("SOAP")) {
//            SoapSchemaOperation soapSchemaOperation = new SoapSchemaOperation();
//            modelSchema.put("operationConfig", soapSchemaOperation);
//        }
        if (modelSchema != null) {
            modelEntity.setModelSchema(JSON.toJSONString(modelSchema));
        } else {
            modelEntity.setModelSchema(JSON.toJSONString(new HashMap<>()));
        }
        modelEntity.setType(model.getModelType());
        modelEntity.setModelKey(model.getModelKey());
        modelEntity.setDescription(model.getDescription());
        modelEntity.setModuleId(model.getModuleId());
        modelEntity.setTargetId(model.getTargetId());
        modelEntity.setTargetType(model.getTargetType());
        return modelEntity;
    }

    private ModelDTO buildModel(ModelEntity entity) {
        ModelDTO model = new ModelDTO();
        model.setDescription(entity.getDescription());
        model.setModuleId(entity.getModuleId());
        model.setName(entity.getName());

        Map modelSchema = JSON.parseObject(entity.getModelSchema(), Map.class);
        if (modelSchema != null) {
            model.setModelSchema(JSON.parseObject(entity.getModelSchema(), Map.class));
        } else {
            model.setModelSchema(new HashMap<>());
        }

        model.setModelType(entity.getType());
        model.setModelKey(entity.getModelKey());
        model.setTargetId(entity.getTargetId());
        model.setTargetType(entity.getTargetType());
        model.setId(entity.getId());
        return model;
    }

    private Map<String, ClassificationModel> buildClassificationModel(List<ModelDTO> models) {
        Map<String, ClassificationModel> classificationModels = new HashMap<>();
        models.forEach(model -> {
            if (model.getTargetType() == ModelTargetType.Service && model.getTargetId() != null) {
                ServiceEntity serviceEntity = serviceRepository.findById(Long.valueOf(model.getTargetId())).get();
                String serviceName = serviceEntity.getName();
                ClassificationModel classificationModel = classificationModels.containsKey(serviceName) ? new ClassificationModel() : classificationModels.get(serviceName);
                List<ModelDTO> modelList = CollectionUtils.isEmpty(classificationModel.getModels()) ? new ArrayList<>() : classificationModel.getModels();
                modelList.add(model);
                classificationModel.setModels(modelList);
                classificationModel.setName(serviceName);
                ServiceType serviceType = serviceEntity.getType().equals(DalaranConsoleConstants.SOAP_CONNECTOR) ? ServiceType.SOAP : ServiceType.SWAGGER;
                classificationModel.setServiceType(serviceType);
                classificationModels.put(serviceName, classificationModel);
            } else {
                ClassificationModel classificationModel = classificationModels.containsKey(COMMON_MODEL) ? new ClassificationModel() : classificationModels.get(COMMON_MODEL);
                List<ModelDTO> modelList = CollectionUtils.isEmpty(classificationModel.getModels()) ? new ArrayList<>() : classificationModel.getModels();
                modelList.add(model);
                classificationModel.setModels(modelList);
                classificationModel.setName(COMMON_MODEL);
                classificationModel.setServiceType(ServiceType.COMMON);
                classificationModels.put(COMMON_MODEL, classificationModel);
            }
        });
        return classificationModels;
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
            case DalaranConsoleConstants.JAVA_LONG:
                return FieldType.INTEGER;
            case DalaranConsoleConstants.JAVA_STRING:
                return FieldType.STRING;
            case DalaranConsoleConstants.JSON_OBJECT:
                return FieldType.OBJECT;
            case DalaranConsoleConstants.JSON_ARRAY:
                return FieldType.ARRAY;
            case DalaranConsoleConstants.JAVA_FLOAT:
            case DalaranConsoleConstants.JAVA_DOUBLE:
            case DalaranConsoleConstants.JAVA_MATH_BIGDECIMAL:
                return FieldType.FLOAT;
            case DalaranConsoleConstants.JAVA_BOOLEAN:
                return FieldType.BOOLEAN;
        }
        return FieldType.STRING;
    }
}
