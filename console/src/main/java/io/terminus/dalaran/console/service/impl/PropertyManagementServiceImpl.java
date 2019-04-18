package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.model.PropertyModel;
import io.terminus.dalaran.console.model.query.PropertyQuery;
import io.terminus.dalaran.console.service.PropertyManagementService;
import io.terminus.dalaran.console.service.jpa.PropertyQueryService;
import io.terminus.dalaran.entity.PropertyEntity;
import io.terminus.dalaran.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jingdi on 2019/4/16
 */
@Service
public class PropertyManagementServiceImpl implements PropertyManagementService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyQueryService propertyQueryService;

    @Override
    public void createProperty(PropertyModel propertyModel) {
        propertyRepository.save(buildEntity(propertyModel));
    }

    @Override
    public void updateProperty(PropertyModel propertyModel) {
        propertyRepository.save(buildEntity(propertyModel));
    }

    @Override
    public void deleteProperty(Long id) {
        propertyRepository.delete(id);
    }

    @Override
    public List<PropertyModel> list() {
        List<PropertyEntity> entities =  propertyRepository.findAll();
        List<PropertyModel> models = new ArrayList<>();
        for (PropertyEntity entity : entities) {
            models.add(buildModel(entity));
        }
        return models;
    }

    @Override
    public List<PropertyModel> queryProperties(PropertyQuery query) {
        List<PropertyEntity> entities = propertyQueryService.query(query);
        List<PropertyModel> models = new ArrayList<>();
        for (PropertyEntity entity : entities) {
            models.add(buildModel(entity));
        }
        return models;
    }

    private PropertyModel buildModel(PropertyEntity entity) {
        PropertyModel model = new PropertyModel();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setValue(entity.getValue());
        model.setDescription(entity.getDescription());
        return model;
    }

    private PropertyEntity buildEntity(PropertyModel model) {
        PropertyEntity propertyEntity;
        Long id = model.getId();
        if (id == null) {
            propertyEntity = new PropertyEntity();
        } else {
            propertyEntity = propertyRepository.findOne(id);
        }
        propertyEntity.setName(model.getName());
        propertyEntity.setValue(model.getValue());
        propertyEntity.setDescription(model.getDescription());
        propertyEntity.setUpdatedAt(new Date());
        return propertyEntity;
    }
}
