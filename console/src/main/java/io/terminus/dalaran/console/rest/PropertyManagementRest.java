package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.OnException;
import io.terminus.dalaran.console.service.PropertyManagementService;
import io.terminus.dalaran.core.resource.property.PropertyService;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.common.OSSAccount;
import io.terminus.dalaran.model.dto.PropertyDTO;
import io.terminus.dalaran.model.query.PropertyQuery;
import io.terminus.dalaran.rest.read.PropertyReadAPI;
import io.terminus.dalaran.rest.write.PropertyWriteAPI;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PropertyManagementRest implements PropertyReadAPI, PropertyWriteAPI {

    @Autowired
    private PropertyManagementService propertyManagementService;

    @Autowired
    private io.terminus.dalaran.core.oss.OSSAccount oss;

    @Autowired
    private PropertyService propertyService;

    @Override
    @OnException(code = ResponseMessage.PROPERTY_CREATE_ERROR)
    public Long create(@RequestBody PropertyDTO model) {
        return propertyManagementService.createProperty(model);
    }

    @Override
    @OnException(code = ResponseMessage.PROPERTY_UPDATE_ERROR)
    public PropertyDTO update(@RequestBody PropertyDTO model) {
        return propertyManagementService.updateProperty(model);
    }

    @Override
    @OnException(code = ResponseMessage.PROPERTY_DELETE_ERROR)
    public void deleteById(@RequestBody Long id) {
        propertyManagementService.deleteProperty(id);
    }

    @Override
    @OnException(code = ResponseMessage.PROPERTY_QUERY_ERROR)
    public List<PropertyDTO> query(PropertyQuery query) {
        return propertyManagementService.queryProperties(query);
    }

    @Override
    @OnException(code = ResponseMessage.PROCESSOR_QUERY_ERROR)
    public List<PropertyDTO> list() {
        return propertyManagementService.list();
    }

    @Override
    public OSSAccount getOSSAccount() {
        OSSAccount ossAccount = new OSSAccount();
        try {
            BeanUtils.copyProperties(ossAccount, oss);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ossAccount;
    }

    @Override
    public BasicResponse getDalaranMarket() {
        return new BasicResponse(true, propertyService.getMarketHost());
    }
}
