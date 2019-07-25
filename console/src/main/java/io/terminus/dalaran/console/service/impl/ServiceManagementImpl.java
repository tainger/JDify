package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.console.entity.ServiceEntity;
import io.terminus.dalaran.console.model.dto.BasicServiceInfo;
import io.terminus.dalaran.console.model.dto.ModelDTO;
import io.terminus.dalaran.console.model.dto.ServiceDTO;
import io.terminus.dalaran.console.repository.ModelRepository;
import io.terminus.dalaran.console.repository.ServiceRepository;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.console.service.ServiceManagement;
import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.component.model.ServiceOperation;
import io.terminus.dalaran.core.component.model.ServiceOperationModel;
import io.terminus.dalaran.core.config.ServiceInfo;
import io.terminus.dalaran.core.context.DalaranServiceContext;
import io.terminus.dalaran.core.model.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceManagementImpl implements ServiceManagement {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private DalaranServiceContext serviceContext;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ModelManagementService modelManagementService;

    @Override
    @Transactional
    public Long create(ServiceDTO serviceDTO) {
        ServiceEntity entity = toEntity(serviceDTO);
        serviceRepository.save(entity);
        return entity.getId();
    }

    @Override
    public ServiceDTO update(ServiceDTO serviceDTO) {
        ServiceEntity entity = toEntity(serviceDTO);
        serviceRepository.save(entity);
        return toDTO(entity);
    }

    @Override
    public void delete(Long serviceId) {
        serviceRepository.delete(serviceId);
    }

    @Override
    public ServiceDTO detail(Long serviceId) {
        return toDTO(serviceRepository.findOne(serviceId));
    }

    @Override
    public List<ServiceDTO> list() {
        return serviceRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<String> listOperation(Long serviceId) {
        ServiceEntity entity = serviceRepository.findOne(serviceId);
        ServiceInfo serviceInfo = serviceContext.getServiceInfo(entity.getType());
        Object serviceConfig = JSON.parseObject(entity.getServiceConfig(), serviceInfo.getServiceConfigType());
        return serviceContext.getService(entity.getType()).operations(serviceConfig);
    }

    @Override
    public List<BasicServiceInfo> listBasicInfoByModuleId(Long moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BasicServiceInfo> criteriaQuery = builder.createQuery(BasicServiceInfo.class);
        Root<ServiceEntity> root = criteriaQuery.from(ServiceEntity.class);
        criteriaQuery.multiselect(root.get("id"), root.get("moduleId"), root.get("type"), root.get("name"))
                .where(builder.equal(root.get("moduleId"), moduleId));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    private ServiceDTO toDTO(ServiceEntity entity) {
        ServiceDTO dto = new ServiceDTO();
        dto.setId(entity.getId());
        dto.setModuleId(entity.getModuleId());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setDescription(entity.getDescription());
        dto.setImportConfig(JSON.parseObject(entity.getImportConfig(), Map.class));
        dto.setServiceConfig(JSON.parseObject(entity.getServiceConfig(), Map.class));
        return dto;
    }

    private ServiceEntity toEntity(ServiceDTO dto) {
        String type = dto.getType();
        Long moduleId = dto.getModuleId();
        Long serviceId = dto.getId();
        ServiceEntity entity = new ServiceEntity();
        entity.setId(serviceId);
        entity.setModuleId(moduleId);
        entity.setName(dto.getName());
        entity.setType(type);
        entity.setDescription(dto.getDescription());
        String importConfigJson = JSON.toJSONString(dto.getImportConfig());
        entity.setImportConfig(importConfigJson);

        Class importConfigType = serviceContext.getServiceInfo(type).getImportConfigType();
        Object importConfig = JSON.parseObject(importConfigJson, importConfigType);
        DalaranService dalaranService = serviceContext.getService(dto.getType());
        Object serviceConfig = dalaranService.importConfig(importConfig);

        Map<String, Long> models = new HashMap<>();
        List<String> operationKeys = dalaranService.operations(serviceConfig);
        operationKeys.forEach(operationKey -> {
            ServiceOperation operation = dalaranService.getOperationConfig(serviceConfig, operationKey);
            ServiceOperationModel operationModel = dalaranService.buildOperationModel(importConfig, operation);
            Long inModelId = buildModel(operationModel.getInModel(), operationModel.getInputName(), moduleId, serviceId, models);
            Long outModelId = buildModel(operationModel.getOutModel(), operationModel.getOutputName(), moduleId, serviceId, models);
            operation.setInModelId(inModelId);
            operation.setOutModelId(outModelId);
        });

        entity.setServiceConfig(JSON.toJSONString(serviceConfig));
        return entity;
    }

    private Long buildModel(MessageModel messageModel, String modelName, Long moduleId, Long serviceId, Map<String, Long> models) {
        if (models.containsKey(modelName)) {
            return models.get(modelName);
        }
        ModelDTO model = new ModelDTO();
        model.setName(modelName);
        model.setModuleId(moduleId);
        model.setServiceId(serviceId);
        model.setModelType(messageModel.getModelType());
        model.setModelSchema(JSON.parseObject(JSON.toJSONString(messageModel.getModelSchema()), Map.class));

        ModelEntity entity = modelManagementService.getByNameAndServiceId(modelName, serviceId);
        if (entity == null) {
            Long id = modelManagementService.createModel(model);
            models.put(modelName, id);
            return id;
        } else {
            Long id = entity.getId();
            model.setId(id);
            modelManagementService.updateModel(model);
            return id;
        }
    }
}
