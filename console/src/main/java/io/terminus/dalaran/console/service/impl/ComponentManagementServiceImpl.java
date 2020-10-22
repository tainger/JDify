package io.terminus.dalaran.console.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.terminus.dalaran.console.service.*;
import io.terminus.dalaran.model.common.BasicComponentType;
import io.terminus.dalaran.model.dto.*;
import io.terminus.dalaran.model.dto.flow.SubFlowDTO;
import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComponentManagementServiceImpl implements ComponentManagementService {

    @Autowired
    private FlowManagementService flowManagementService;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private FunctionService functionService;

    @Autowired
    private ClientManagementService clientManagementService;

    @Autowired
    private ModelManagementService modelManagementService;

    @Autowired
    private ModuleManagementService moduleManagementService;

    @Autowired
    private SubFlowManagementService subFlowManagementService;

    @Autowired
    private ServiceManagement serviceManagement;

    @Override
    public Long create(ComponentDTO componentDTO) {
        ObjectMapper objectMapper = new ObjectMapper();
        String componentConfig = componentDTO.getConfig();
        try {
            switch (componentDTO.getType()){
                case Flow:
                    return flowManagementService.createFlow(objectMapper.readValue(componentConfig, TriggerFlowDTO.class));
                case Model:
                    return modelManagementService.createModel(objectMapper.readValue(componentConfig, ModelDTO.class));
                case Client:
                    return clientManagementService.create(objectMapper.readValue(componentConfig, ClientDTO.class));
                case SubFlow:
                    return subFlowManagementService.createFlow(objectMapper.readValue(componentConfig, SubFlowDTO.class));
                case Function:
                    return functionService.create(objectMapper.readValue(componentConfig, FunctionDTO.class));
                case Connector:
                    return connectorService.create(objectMapper.readValue(componentConfig, ConnectorDTO.class));
                case Service:
                    return serviceManagement.create(objectMapper.readValue(componentConfig, ServiceDTO.class));
                case Module:
                    return moduleManagementService.createModule(objectMapper.readValue(componentConfig, ModuleDTO.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("create component error! ", e.getCause());
        }
        return null;
    }

    @Override
    public Object update(ComponentDTO componentDTO) {
        ObjectMapper objectMapper = new ObjectMapper();
        String componentConfig = componentDTO.getConfig();
        try {
            switch (componentDTO.getType()){
                case Flow:
                    return flowManagementService.updateFlow(objectMapper.readValue(componentConfig, TriggerFlowDTO.class));
                case Model:
                    return modelManagementService.updateModel(objectMapper.readValue(componentConfig, ModelDTO.class));
                case Client:
                    return clientManagementService.update(objectMapper.readValue(componentConfig, ClientDTO.class));
                case SubFlow:
                    return subFlowManagementService.updateFlow(objectMapper.readValue(componentConfig, SubFlowDTO.class));
                case Function:
                    return functionService.update(objectMapper.readValue(componentConfig, FunctionDTO.class));
                case Connector:
                    return connectorService.update(objectMapper.readValue(componentConfig, ConnectorDTO.class));
                case Service:
                    return serviceManagement.update(objectMapper.readValue(componentConfig, ServiceDTO.class));
                case Module:
                    return moduleManagementService.updateModule(objectMapper.readValue(componentConfig, ModuleDTO.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("update component error! ", e.getCause());
        }
        return null;
    }

    @Override
    public void delete(BasicComponentType componentType, Long componentId) {
        try {
            switch (componentType){
                case Flow:
                    flowManagementService.deleteFlow(componentId);
                    break;
                case Model:
                    modelManagementService.deleteModel(componentId);
                    break;
                case Client:
                    clientManagementService.delete(componentId);
                    break;
                case SubFlow:
                    subFlowManagementService.deleteFlow(componentId);
                    break;
                case Function:
                    functionService.delete(componentId);
                    break;
                case Connector:
                    connectorService.delete(componentId);
                    break;
                case Service:
                    serviceManagement.delete(componentId);
                    break;
                case Module:
                    moduleManagementService.deleteModule(componentId);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("delete component error! ", e.getCause());
        }
    }

    @Override
    public Object detail(BasicComponentType componentType, Long componentId) {
        return null;
    }
}
