package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.api.rest.PropertyRestAPI;
import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.DalaranException;
import io.terminus.dalaran.console.service.PropertyManagementService;
import io.terminus.dalaran.model.dto.PropertyDTO;
import io.terminus.dalaran.model.query.PropertyQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PropertyManagementRest implements PropertyRestAPI {

    @Autowired
    private PropertyManagementService propertyManagementService;

    @Override
    @DalaranException(value = ResponseMessage.PROPERTY_CREATE_ERROR)
    public Long create(@RequestBody PropertyDTO model) {
        return propertyManagementService.createProperty(model);
    }

    @Override
    @DalaranException(value = ResponseMessage.PROPERTY_UPDATE_ERROR)
    public PropertyDTO update(@RequestBody PropertyDTO model) {
        return propertyManagementService.updateProperty(model);
    }

    @Override
    @DalaranException(value = ResponseMessage.PROPERTY_DELETE_ERROR)
    public void delete(@RequestBody Long id) {
        propertyManagementService.deleteProperty(id);
    }

    @Override
    @DalaranException(value = ResponseMessage.PROPERTY_QUERY_ERROR)
    public List<PropertyDTO> query(PropertyQuery query) {
        return propertyManagementService.queryProperties(query);
    }

    @Override
    @DalaranException(value = ResponseMessage.PROCESSOR_QUERY_ERROR)
    public List<PropertyDTO> list() {
        return propertyManagementService.list();
    }
}
