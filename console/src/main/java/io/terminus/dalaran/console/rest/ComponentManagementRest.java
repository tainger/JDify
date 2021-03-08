package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.service.ComponentManagementService;
import io.terminus.dalaran.model.CreateResponse;
import io.terminus.dalaran.model.common.BasicComponentType;
import io.terminus.dalaran.model.dto.ComponentDTO;
import io.terminus.dalaran.rest.write.ComponentWriteAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ComponentManagementRest implements ComponentWriteAPI {

    @Autowired
    private ComponentManagementService componentManagementService;

    @Override
    public CreateResponse create(ComponentDTO componentDTO) {
        return new CreateResponse(componentManagementService.create(componentDTO));
    }

    @Override
    public Object update(ComponentDTO componentDTO) {
        return componentManagementService.update(componentDTO);
    }

    @Override
    public void deleteById(BasicComponentType type, String id) {
        componentManagementService.delete(type, id);
    }
}
