package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.dto.PropertyDTO;
import io.terminus.dalaran.console.model.query.PropertyQuery;

import java.util.List;

/**
 * Created by jingdi on 2019/4/16
 */
public interface PropertyManagementService {

    Long createProperty(PropertyDTO propertyModel);

    PropertyDTO updateProperty(PropertyDTO propertyModel);

    void deleteProperty(Long id);

    List<PropertyDTO> list();

    List<PropertyDTO> queryProperties(PropertyQuery query);
}
