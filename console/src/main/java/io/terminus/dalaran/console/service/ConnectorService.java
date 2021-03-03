package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.ConnectorDTO;
import io.terminus.dalaran.model.dto.basic.BasicConnectorInfo;

import java.util.List;

public interface ConnectorService {

    String create(ConnectorDTO connectorDTO);

    ConnectorDTO update(ConnectorDTO connectorDTO);

    void delete(String connectorId);

    ConnectorDTO detail(String connectorId);

    List<BasicConnectorInfo> listBasicInfoByModuleId(String moduleId);

    List<BasicConnectorInfo> listBasicInfoByComponent(String connectorType);
}
