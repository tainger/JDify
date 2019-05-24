package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.DalaranServiceContext;
import io.terminus.dalaran.console.model.dto.ServiceDTO;
import io.terminus.dalaran.console.service.ServiceManagement;
import io.terminus.dalaran.entity.manage.ServiceEntity;
import io.terminus.dalaran.model.config.ServiceInfo;
import io.terminus.dalaran.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServiceManagementImpl implements ServiceManagement {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private DalaranServiceContext serviceContext;

    @Override
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

    private ServiceDTO toDTO(ServiceEntity entity) {
        ServiceDTO dto = new ServiceDTO();
        dto.setId(entity.getId());
        dto.setModuleId(entity.getModuleId());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setDescription(entity.getDescription());
        dto.setImportConfig(JSON.parseObject(entity.getImportConfig(), Map.class));
        return dto;
    }

    private ServiceEntity toEntity(ServiceDTO dto) {
        ServiceEntity entity = new ServiceEntity();
        entity.setId(dto.getId());
        entity.setModuleId(dto.getModuleId());
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setDescription(dto.getDescription());
        String importConfigJson = JSON.toJSONString(dto.getImportConfig());
        entity.setImportConfig(importConfigJson);

        Class importConfigType = serviceContext.getServiceInfo(dto.getType()).getImportConfigType();
        Object importConfig = JSON.parseObject(importConfigJson, importConfigType);

        Object serviceConfig = serviceContext.getService(dto.getType()).importConfig(importConfig);
        entity.setServiceConfig(JSON.toJSONString(serviceConfig));
        return entity;
    }
}
