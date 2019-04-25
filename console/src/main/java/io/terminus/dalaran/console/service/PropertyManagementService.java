package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.PropertyModel;
import io.terminus.dalaran.console.model.query.PropertyQuery;

import java.util.List;

/**
 * Created by jingdi on 2019/4/16
 */
public interface PropertyManagementService {

    Long createProperty(PropertyModel propertyModel);

    PropertyModel updateProperty(PropertyModel propertyModel);

    void deleteProperty(Long id);

    List<PropertyModel> list();

    List<PropertyModel> queryProperties(PropertyQuery query);
}
