package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.ComponentType;
import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.OnExceptionMessage;
import io.terminus.dalaran.console.service.ConnectorService;
import io.terminus.dalaran.model.dto.ConnectorDTO;
import io.terminus.dalaran.model.dto.basic.BasicConnectorInfo;
import io.terminus.dalaran.rest.read.ConnectorReadAPI;
import io.terminus.dalaran.rest.write.ConnectorWriteAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConnectorRest implements ConnectorReadAPI, ConnectorWriteAPI {

    @Autowired
    private ConnectorService connectorService;

    @Override
    @OnExceptionMessage(value = ResponseMessage.CONNECTOR_CREATE_ERROR)
    public Long create(@RequestBody ConnectorDTO connectorDTO) {
        return connectorService.create(connectorDTO);
    }

    @Override
    @OnExceptionMessage(value = ResponseMessage.CONNECTOR_UPDATE_ERROR)
    public ConnectorDTO update(@RequestBody ConnectorDTO connectorDTO) {
        return connectorService.update(connectorDTO);
    }

    @Override
    @OnExceptionMessage(value = ResponseMessage.CONNECTOR_DELETE_ERROR)
    public void deleteById(@PathVariable Long id) {
        connectorService.delete(id);
    }

    @Override
    @OnExceptionMessage(value = ResponseMessage.CONNECTOR_QUERY_ERROR)
    public ConnectorDTO detail(@PathVariable Long id) {
        return connectorService.detail(id);
    }

    @Override
    @OnExceptionMessage(value = ResponseMessage.CONNECTOR_QUERY_ERROR)
    public List<BasicConnectorInfo> selectOptions(@RequestParam ComponentType componentType, @RequestParam String componentName) {
        return connectorService.listBasicInfoByComponent(componentType, componentName);
    }
}
