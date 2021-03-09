package io.terminus.dalaran.console.service;


import io.terminus.dalaran.model.common.BasicComponentType;
import io.terminus.dalaran.model.dto.ComponentDTO;

public interface ComponentManagementService {

    String create(ComponentDTO componentDTO);

    Object update(ComponentDTO componentDTO);

    void delete(BasicComponentType componentType, String componentId);

    Object detail(BasicComponentType componentType, String componentId);
}
