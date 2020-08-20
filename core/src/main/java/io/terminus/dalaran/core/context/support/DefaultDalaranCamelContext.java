package io.terminus.dalaran.core.context.support;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.core.context.*;
import io.terminus.dalaran.core.flow.DalaranFlowBuilder;
import io.terminus.dalaran.core.flow.DalaranRoute;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowFragment;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.terminus.dalaran.DalaranConstants.*;

@Slf4j
public class DefaultDalaranCamelContext implements DalaranContext<DalaranRoute> {

    @Autowired
    private DalaranModelTypeContext modelTypeContext;
    @Autowired
    private DalaranComponentContext componentContext;
    @Autowired
    private DalaranFunctionContext functionContext;
    @Autowired
    private DalaranServiceContext serviceContext;
    @Autowired
    private DalaranClientContext clientContext;

    private final DalaranFlowBuilder<DalaranRoute> flowBuilder;

    private final CamelContext camelContext;

    public DefaultDalaranCamelContext(CamelContext camelContext, DalaranFlowBuilder flowBuilder) {
        this.camelContext = camelContext;
        this.flowBuilder = flowBuilder;
    }

    @Override
    public void removeFlow(String flowId) {
        try {
            camelContext.removeRoute(flowId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeFlows(List<String> flowIds) {
        flowIds.forEach(this::removeFlow);
    }

    @Override
    public void removeAllFlow() throws Exception {
        camelContext.removeRouteDefinitions(camelContext.getRouteDefinitions());
    }

    @Override
    public void addTriggerFlow(TriggerFlow flow) {
        try {
            camelContext.removeRoute(FLOW_PREFIX + flow.getId());
            DalaranRoute route = flowBuilder.buildTriggerFlow(flow, camelContext);
            log.info("load trigger flow [{}], steps: {}", route.getId(), route.getSteps());
            camelContext.addRouteDefinition(route);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTriggerFlows(List<TriggerFlow> flows) {
        flows.forEach(this::addTriggerFlow);
    }

    @Override
    public void addTestFlow(BasicFlow flow) {
        try {
            camelContext.removeRoute(TEST_FLOW_PREFIX + flow.getRouteId());
            DalaranRoute route = flowBuilder.buildTestFLow(flow);
            log.info("load test flow [{}], steps: {}", route.getId(), route.getSteps());
            camelContext.addRouteDefinition(route);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTestFlows(List<BasicFlow> flows) {
        flows.forEach(this::addTestFlow);
    }

    @Override
    public void addTestSubFLow(SubFlow flow) {
        try {
            camelContext.removeRoute(DalaranConstants.TEST_SUB_FLOW_PREFIX + flow.getRouteId());
            DalaranRoute route = flowBuilder.buildTestFLow(flow);
            log.info("load test flow [{}], steps: {}", route.getId(), route.getSteps());
            camelContext.addRouteDefinition(route);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTestSubFLows(List<SubFlow> flows) {
        flows.forEach(this::addTestSubFLow);
    }

    @Override
    public void addSubFlow(SubFlow flow) {
        try {
            camelContext.removeRoute(flow.getRouteId());
            DalaranRoute route = flowBuilder.buildSubFLow(flow);
            log.info("load sub flow [{}], steps: {}", route.getId(), route.getSteps());
            camelContext.addRouteDefinition(route);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addSubFlows(List<SubFlow> flows) {
        flows.forEach(this::addSubFlow);
    }

    @Override
    public void addFragmentFlow(FlowFragment flow) {
        try {
            camelContext.removeRoute(flow.getRouteId());
            DalaranRoute route = flowBuilder.buildFlowFragment(flow);
            log.info("load fragment flow [{}], steps: {}", route.getId(), route.getSteps());
            camelContext.addRouteDefinition(route);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String testFlow(Long flowId, String body) {
        String recordId = nextRecordId();
        if (camelContext.getRoute(TEST_FLOW_PREFIX + FLOW_PREFIX + flowId) == null) {
            throw new RuntimeException("Test error! This flow can't be loaded into test service, please check it.");
        }
        try {
            DefaultProducerTemplate template = (DefaultProducerTemplate) camelContext.createProducerTemplate();
            template.sendBodyAndProperty(TEST_FLOW_DIRECT_PREFIX + FLOW_PREFIX + flowId, body, DalaranConstants.TEST_FLOW_RECORD_ID_HEADER, recordId);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return recordId;
    }

    @Override
    public String testSubFlow(Long subFlowId, String body) {
        String recordId = nextRecordId();
        if (camelContext.getRoute(TEST_SUB_FLOW_PREFIX + FLOW_PREFIX + subFlowId) == null) {
            throw new RuntimeException("Test error! This sub flow can't be loaded into test service, please check it.");
        }
        try {
            DefaultProducerTemplate template = (DefaultProducerTemplate) camelContext.createProducerTemplate();
            template.sendBodyAndProperty(TEST_SUB_FLOW_DIRECT_PREFIX + FLOW_PREFIX + subFlowId, body, DalaranConstants.TEST_FLOW_RECORD_ID_HEADER, recordId);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return recordId;
    }

    @Override
    public void addRoute(DalaranRoute route) {
        try {
            camelContext.addRouteDefinition(route);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public DalaranComponentContext getDalaranComponentContext() {
        return componentContext;
    }

    @Override
    public DalaranModelTypeContext getDalaranModelTypeContext() {
        return modelTypeContext;
    }

    @Override
    public DalaranServiceContext getDalaranServiceContext() {
        return serviceContext;
    }

    @Override
    public DalaranFunctionContext getDalaranFunctionContext() {
        return functionContext;
    }

    @Override
    public DalaranClientContext getDalaranClientContext() {
        return clientContext;
    }

    @Override
    public DalaranFlowBuilder<DalaranRoute> getDalaranFlowBuilder() {
        return flowBuilder;
    }

    // TODO 这里可以考虑换一下 camel 的 uuid 生成器
    private String nextRecordId() {
        return RandomStringUtils.randomAlphanumeric(32);
    }

}
