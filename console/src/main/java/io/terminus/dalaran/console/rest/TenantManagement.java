package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.DalaranConsoleConstants;
import io.terminus.dalaran.console.service.TenantService;
import io.terminus.dalaran.core.resource.property.PropertyService;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.rest.write.TenantApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
public class TenantManagement implements TenantApi {

    @Autowired
    private  PropertyService propertyService;

    @Autowired
    private TenantService tenantService;

    @Override
    public BasicResponse register() {
        return  tenantService.register();
    }
}
