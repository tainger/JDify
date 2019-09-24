package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.ComponentType;
import io.terminus.dalaran.api.rest.ConnectorRestAPI;
import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.DalaranException;
import io.terminus.dalaran.console.service.ConnectorService;
import io.terminus.dalaran.model.dto.ConnectorDTO;
import io.terminus.dalaran.model.dto.basic.BasicConnectorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConnectorRest implements ConnectorRestAPI {

    @Autowired
    private ConnectorService connectorService;

    @Override
    @DalaranException(value = ResponseMessage.CONNECTOR_CREATE_ERROR)
    public Long create(@RequestBody ConnectorDTO connectorDTO) {
        return connectorService.create(connectorDTO);
    }

    @Override
    @DalaranException(value = ResponseMessage.CONNECTOR_UPDATE_ERROR)
    public ConnectorDTO update(@RequestBody ConnectorDTO connectorDTO) {
        return connectorService.update(connectorDTO);
    }

    @Override
    @DalaranException(value = ResponseMessage.CONNECTOR_DELETE_ERROR)
    public void create(@PathVariable Long id) {
        connectorService.delete(id);
    }

    @Override
    @DalaranException(value = ResponseMessage.CONNECTOR_QUERY_ERROR)
    public ConnectorDTO detail(@PathVariable Long id) {
        return connectorService.detail(id);
    }

    @Override
    @DalaranException(value = ResponseMessage.CONNECTOR_QUERY_ERROR)
    public List<BasicConnectorInfo> selectOptions(@RequestParam ComponentType componentType, @RequestParam String componentName) {
        return connectorService.listBasicInfoByComponent(componentType, componentName);
    }
}
