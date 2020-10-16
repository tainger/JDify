package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.entity.PropertyEntity;
import io.terminus.dalaran.console.repository.PropertyRepository;
import io.terminus.dalaran.console.service.PropertyManagementService;
import io.terminus.dalaran.console.service.jpa.PropertyQueryService;
import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import io.terminus.dalaran.model.dto.PropertyDTO;
import io.terminus.dalaran.model.query.PropertyQuery;
import io.terminus.draco.web.autoconfig.context.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jingdi on 2019/4/16
 */
@Service
@Transactional
public class PropertyManagementServiceImpl implements PropertyManagementService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyQueryService propertyQueryService;

    @Override
    public Long createProperty(PropertyDTO propertyModel) {
        PropertyEntity propertyEntity = buildEntity(propertyModel);
        setCreatedBy(propertyEntity);
        return propertyRepository.save(propertyEntity).getId();
    }

    @Override
    public PropertyDTO updateProperty(PropertyDTO propertyModel) {
        PropertyEntity propertyEntity = buildEntity(propertyModel);
        setUpdatedBy(propertyEntity);
        propertyRepository.save(propertyEntity);
        return propertyModel;
    }

    @Override
    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }

    @Override
    public List<PropertyDTO> list() {
        List<PropertyEntity> entities = propertyRepository.findAll();
        List<PropertyDTO> models = new ArrayList<>();
        for (PropertyEntity entity : entities) {
            models.add(buildModel(entity));
        }
        return models;
    }

    @Override
    public List<PropertyDTO> queryProperties(PropertyQuery query) {
        List<PropertyEntity> entities = propertyQueryService.query(query);
        List<PropertyDTO> models = new ArrayList<>();
        for (PropertyEntity entity : entities) {
            models.add(buildModel(entity));
        }
        return models;
    }

    private PropertyDTO buildModel(PropertyEntity entity) {
        PropertyDTO model = new PropertyDTO();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setValue(entity.getValue());
        model.setDescription(entity.getDescription());
        return model;
    }

    private PropertyEntity buildEntity(PropertyDTO model) {
        PropertyEntity propertyEntity;
        Long id = model.getId();
        if (id == null) {
            propertyEntity = new PropertyEntity();
        } else {
            propertyEntity = propertyRepository.findById(id).get();
        }
        String name = model.getName();
        if (StringUtils.isNoneBlank(name)) {
            propertyEntity.setName(name);
        } else {
            propertyEntity.setName("Dalaran Property");
        }
        propertyEntity.setValue(model.getValue());
        propertyEntity.setDescription(model.getDescription());
        return propertyEntity;
    }

    private void setCreatedBy(PropertyEntity propertyEntity){
        if(UserContext.getUserInfo().getUsername()!=null){
            propertyEntity.setCreatedBy(UserContext.getUserInfo().getUsername());
        }
    }

    private void setUpdatedBy(PropertyEntity propertyEntity){
        if(UserContext.getUserInfo().getUsername()!=null){
            propertyEntity.setUpdatedBy(UserContext.getUserInfo().getUsername());
        }
    }
}
