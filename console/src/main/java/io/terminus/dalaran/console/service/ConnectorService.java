package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.dto.BasicConnectorInfo;
import io.terminus.dalaran.console.model.dto.ConnectorDTO;

import java.util.List;

public interface ConnectorService {

    Long create(ConnectorDTO connectorDTO);

    ConnectorDTO update(ConnectorDTO connectorDTO);

    void delete(Long connectorId);

    ConnectorDTO detail(Long connectorId);

    List<BasicConnectorInfo> listBasicInfoByModuleId(Long moduleId);
}
