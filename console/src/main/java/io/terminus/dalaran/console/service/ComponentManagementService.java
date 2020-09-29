package io.terminus.dalaran.console.service;


import io.terminus.dalaran.model.common.BasicComponentType;
import io.terminus.dalaran.model.dto.ComponentDTO;

public interface ComponentManagementService {

    Long create(ComponentDTO componentDTO);

    Object update(ComponentDTO componentDTO);

    void delete(BasicComponentType componentType, Long componentId);

    Object detail(BasicComponentType componentType, Long componentId);
}
