package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.OnExceptionMessage;
import io.terminus.dalaran.console.service.PropertyManagementService;
import io.terminus.dalaran.model.dto.PropertyDTO;
import io.terminus.dalaran.model.query.PropertyQuery;
import io.terminus.dalaran.rest.read.PropertyReadAPI;
import io.terminus.dalaran.rest.write.PropertyWriteAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PropertyManagementRest implements PropertyReadAPI, PropertyWriteAPI {

    @Autowired
    private PropertyManagementService propertyManagementService;

    @Override
    @OnExceptionMessage(value = ResponseMessage.PROPERTY_CREATE_ERROR)
    public Long create(@RequestBody PropertyDTO model) {
        return propertyManagementService.createProperty(model);
    }

    @Override
    @OnExceptionMessage(value = ResponseMessage.PROPERTY_UPDATE_ERROR)
    public PropertyDTO update(@RequestBody PropertyDTO model) {
        return propertyManagementService.updateProperty(model);
    }

    @Override
    @OnExceptionMessage(value = ResponseMessage.PROPERTY_DELETE_ERROR)
    public void deleteById(@RequestBody Long id) {
        propertyManagementService.deleteProperty(id);
    }

    @Override
    @OnExceptionMessage(value = ResponseMessage.PROPERTY_QUERY_ERROR)
    public List<PropertyDTO> query(PropertyQuery query) {
        return propertyManagementService.queryProperties(query);
    }

    @Override
    @OnExceptionMessage(value = ResponseMessage.PROCESSOR_QUERY_ERROR)
    public List<PropertyDTO> list() {
        return propertyManagementService.list();
    }
}
