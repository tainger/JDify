package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.dto.ConnectorDTO;
import io.terminus.dalaran.console.model.dto.basic.BasicConnectorInfo;
import io.terminus.dalaran.core.component.ComponentType;

import java.util.List;

public interface ConnectorService {

    Long create(ConnectorDTO connectorDTO);

    ConnectorDTO update(ConnectorDTO connectorDTO);

    void delete(Long connectorId);

    ConnectorDTO detail(Long connectorId);

    List<BasicConnectorInfo> listBasicInfoByModuleId(Long moduleId);

    List<BasicConnectorInfo> listBasicInfoByComponent(ComponentType componentType, String componentName);
}
