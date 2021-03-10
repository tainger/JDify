package io.terminus.dalaran.model.dto;

import io.terminus.dalaran.model.dto.basic.*;
import lombok.Data;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@Data
public class ModuleDetailDTO extends ModuleDTO {

    private List<BasicFlowInfo> flows;

    private List<BasicFlowInfo> subFlows;

    private List<BasicModelInfo> models;

    private List<BasicConnectorInfo> connectors;

    private List<BasicLimiterInfo> limiters;

    private List<BasicServiceInfo> services;

    private List<BasicFunctionInfo> functions;

    private List<BasicClientInfo> clients;

    private List<BasicAlarmInfo> alarmRules;
}
