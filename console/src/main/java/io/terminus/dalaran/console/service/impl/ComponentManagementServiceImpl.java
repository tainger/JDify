package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
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
    private LimiterService limiterService;

    @Autowired
    private ServiceManagement serviceManagement;

    @Override
    public Long create(ComponentDTO componentDTO) {
        String componentConfig = componentDTO.getConfig();
        try {
            switch (componentDTO.getType()){
                case Flow:
                    return flowManagementService.createFlow(JSON.parseObject(componentConfig, TriggerFlowDTO.class));
                case Model:
                    return modelManagementService.createModel(JSON.parseObject(componentConfig, ModelDTO.class));
                case Client:
                    return clientManagementService.create(JSON.parseObject(componentConfig, ClientDTO.class));
                case SubFlow:
                    return subFlowManagementService.createFlow(JSON.parseObject(componentConfig, SubFlowDTO.class));
                case Function:
                    return functionService.create(JSON.parseObject(componentConfig, FunctionDTO.class));
                case Connector:
                    return connectorService.create(JSON.parseObject(componentConfig, ConnectorDTO.class));
                case Service:
                    return serviceManagement.create(JSON.parseObject(componentConfig, ServiceDTO.class));
                case Module:
                    return moduleManagementService.createModule(JSON.parseObject(componentConfig, ModuleDTO.class));
                case Limiter:
                    return limiterService.create(JSON.parseObject(componentConfig, LimiterDTO.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("create " + componentDTO.getType().name() + " error! ", e.getCause());
        }
        return null;
    }

    @Override
    public Object update(ComponentDTO componentDTO) {
        String componentConfig = componentDTO.getConfig();
        try {
            switch (componentDTO.getType()){
                case Flow:
                    return flowManagementService.updateFlow(JSON.parseObject(componentConfig, TriggerFlowDTO.class));
                case Model:
                    return modelManagementService.updateModel(JSON.parseObject(componentConfig, ModelDTO.class));
                case Client:
                    return clientManagementService.update(JSON.parseObject(componentConfig, ClientDTO.class));
                case SubFlow:
                    return subFlowManagementService.updateFlow(JSON.parseObject(componentConfig, SubFlowDTO.class));
                case Function:
                    return functionService.update(JSON.parseObject(componentConfig, FunctionDTO.class));
                case Connector:
                    return connectorService.update(JSON.parseObject(componentConfig, ConnectorDTO.class));
                case Service:
                    return serviceManagement.update(JSON.parseObject(componentConfig, ServiceDTO.class));
                case Module:
                    return moduleManagementService.updateModule(JSON.parseObject(componentConfig, ModuleDTO.class));
                case Limiter:
                    return limiterService.update(JSON.parseObject(componentConfig, LimiterDTO.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("update " + componentDTO.getType().name() + " error! ", e.getCause());
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
                case Limiter:
                    limiterService.delete(componentId);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("delete " + componentType.name() + " error! ", e.getCause());
        }
    }

    @Override
    public Object detail(BasicComponentType componentType, Long componentId) {
        return null;
    }
}
