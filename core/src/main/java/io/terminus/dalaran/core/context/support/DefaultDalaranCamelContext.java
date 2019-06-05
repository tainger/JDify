package io.terminus.dalaran.core.context.support;

import io.terminus.dalaran.core.DalaranConstants;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.context.DalaranConverterContext;
import io.terminus.dalaran.core.context.DalaranServiceContext;
import io.terminus.dalaran.core.flow.DalaranFlowBuilder;
import io.terminus.dalaran.core.flow.DalaranRoute;
import io.terminus.dalaran.core.flow.model.BasicFlow;
import io.terminus.dalaran.core.flow.model.FlowFragment;
import io.terminus.dalaran.core.flow.model.SubFlow;
import io.terminus.dalaran.core.flow.model.TriggerFlow;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

import static io.terminus.dalaran.core.DalaranConstants.FLOW_PREFIX;
import static io.terminus.dalaran.core.DalaranConstants.TEST_FLOW_DIRECT_PREFIX;

public class DefaultDalaranCamelContext implements DalaranContext<DalaranRoute> {

    private final DalaranFlowBuilder<DalaranRoute> flowBuilder;
    private final CamelContext camelContext;
    private final DalaranConverterContext converterContext;
    private final DalaranComponentContext componentContext;
    private final DalaranServiceContext serviceContext;


    public DefaultDalaranCamelContext(
            CamelContext camelContext,
            DalaranConverterContext converterContext,
            DalaranComponentContext componentContext,
            DalaranServiceContext serviceContext,
            DalaranFlowBuilder flowBuilder
    ) {
        this.camelContext = camelContext;
        this.flowBuilder = flowBuilder;
        this.serviceContext = serviceContext;
        this.converterContext = converterContext;
        this.componentContext = componentContext;
        try {
            camelContext.setTracing(true);
            camelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            RouteDefinition route = flowBuilder.buildTriggerFlow(flow);
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
            camelContext.removeRoute(DalaranConstants.TEST_FLOW_PREFIX + flow.getRouteId());
            RouteDefinition route = flowBuilder.buildTestFLow(flow);
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
    public void addSubFlow(SubFlow flow) {

        try {
            camelContext.removeRoute(flow.getRouteId());
            RouteDefinition route = flowBuilder.buildSubFLow(flow);
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
            RouteDefinition route = flowBuilder.buildFlowFragment(flow);
            camelContext.addRouteDefinition(route);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addFragmentFlows(List<FlowFragment> flows) {
        flows.forEach(this::addFragmentFlow);
    }

    @Override
    public void testFlow(Long flowId, String body, String recordId) {
        DefaultProducerTemplate template = (DefaultProducerTemplate) camelContext.createProducerTemplate();
        template.sendBodyAndProperty(TEST_FLOW_DIRECT_PREFIX + FLOW_PREFIX + flowId, body, DalaranConstants.TEST_FLOW_RECORD_ID_HEADER, recordId);
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
    public DalaranConverterContext getDalaranConverterContext() {
        return converterContext;
    }

    @Override
    public DalaranServiceContext getDalaranServiceContext() {
        return serviceContext;
    }

}
