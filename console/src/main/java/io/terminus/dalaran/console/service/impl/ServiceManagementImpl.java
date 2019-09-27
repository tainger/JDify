package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.config.ServiceInfo;
import io.terminus.dalaran.console.ServiceDetail;
import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.console.entity.ServiceEntity;
import io.terminus.dalaran.console.repository.ModelRepository;
import io.terminus.dalaran.console.repository.ServiceRepository;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.console.service.ServiceManagement;
import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.model.ServiceOperationModel;
import io.terminus.dalaran.core.context.DalaranServiceContext;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ModelTargetType;
import io.terminus.dalaran.model.component.ServiceOperation;
import io.terminus.dalaran.model.dto.ModelDTO;
import io.terminus.dalaran.model.dto.ServiceDTO;
import io.terminus.dalaran.model.dto.basic.BasicServiceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        ServiceDetail serviceDetail = toEntity(serviceDTO);
        Long serviceId = serviceRepository.save(serviceDetail.getEntity()).getId();
        serviceDetail.setServiceId(serviceId);
        createModels(serviceDetail);
        return serviceId;
    }

    @Override
    public ServiceDTO update(ServiceDTO serviceDTO) {
        ServiceDetail serviceDetail = toEntity(serviceDTO);
        ServiceEntity entity = serviceDetail.getEntity();
        serviceDetail.setServiceId(serviceDTO.getId());
        serviceRepository.save(entity);
        createModels(serviceDetail);
        return toDTO(entity);
    }

    @Override
    public void delete(Long serviceId) {
        serviceRepository.deleteById(serviceId);
    }

    @Override
    public ServiceDTO detail(Long serviceId) {
        return toDTO(serviceRepository.findById(serviceId).get());
    }

    @Override
    public List<ServiceDTO> list() {
        return serviceRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<String> listOperation(Long serviceId) {
        ServiceEntity entity = serviceRepository.findById(serviceId).get();
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

    private ServiceDetail toEntity(ServiceDTO dto) {
        ServiceDetail serviceDetail = new ServiceDetail();
        String type = dto.getType();
        Long moduleId = dto.getModuleId();
        serviceDetail.setModuleId(moduleId);
        Long serviceId = dto.getId();
        ServiceEntity entity = new ServiceEntity();
        if (serviceId != null) {
            entity.setId(serviceId);
        }
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
        entity.setServiceConfig(JSON.toJSONString(serviceConfig));
        serviceDetail.setEntity(entity);
        serviceDetail.setDalaranService(dalaranService);
        serviceDetail.setImportConfig(importConfig);
        serviceDetail.setServiceConfig(serviceConfig);
        return serviceDetail;
    }

    private void createModels(ServiceDetail serviceDetail) {
        Map<String, Long> models = new HashMap<>();
        ServiceEntity entity = serviceDetail.getEntity();
        DalaranService dalaranService = serviceDetail.getDalaranService();
        Object serviceConfig = serviceDetail.getServiceConfig();
        Object importConfig = serviceDetail.getImportConfig();
        Long moduleId = serviceDetail.getModuleId();
        Long serviceId = serviceDetail.getServiceId();
        List<ServiceOperation> operations = dalaranService.operations(serviceConfig);
        operations.forEach(operation -> {
            ServiceOperationModel operationModel = dalaranService.buildOperationModel(importConfig, operation);
            Long inModelId = buildModel(operationModel.getInModel(), operationModel.getInputName(), moduleId, serviceId, models);
            Long outModelId = buildModel(operationModel.getOutModel(), operationModel.getOutputName(), moduleId, serviceId, models);
            operation.setInModelId(inModelId);
            operation.setOutModelId(outModelId);
        });
        entity.setServiceConfig(JSON.toJSONString(serviceConfig));
        serviceRepository.save(entity);
    }

    private Long buildModel(MessageModel messageModel, String modelName, Long moduleId, Long serviceId, Map<String, Long> models) {
        if (models.containsKey(modelName)) {
            return models.get(modelName);
        }
        ModelDTO model = new ModelDTO();
        String targetId = serviceId.toString();
        model.setName(modelName);
        model.setModuleId(moduleId);
        model.setTargetId(targetId);
        model.setTargetType(ModelTargetType.Service);
        model.setModelType(messageModel.getModelType());
        model.setModelSchema(JSON.parseObject(JSON.toJSONString(messageModel.getModelSchema()), Map.class));

        ModelEntity entity = modelManagementService.getByNameAndServiceId(modelName, targetId);
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
