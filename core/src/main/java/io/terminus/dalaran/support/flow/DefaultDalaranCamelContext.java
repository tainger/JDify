package io.terminus.dalaran.support.flow;

import io.terminus.dalaran.DalaranComponentContext;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.DalaranConverterContext;
import io.terminus.dalaran.DalaranServiceContext;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

import static io.terminus.dalaran.DalaranConstants.*;

public class DefaultDalaranCamelContext implements DalaranContext {

    private final FlowBuilder flowBuilder;
    private final CamelContext camelContext;
    private final DalaranConverterContext converterContext;
    private final DalaranComponentContext componentContext;
    private final DalaranServiceContext serviceContext;


    public DefaultDalaranCamelContext(
            CamelContext camelContext,
            DalaranConverterContext converterContext,
            DalaranComponentContext componentContext,
            DalaranServiceContext serviceContext,
            FlowBuilder flowBuilder
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
    public void removeFlow(Long flowId) {
        try {
            camelContext.removeRoute(FLOW_CAMEL_URI_PREFIX + flowId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeFlows(List<Long> flowIds) {
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
            camelContext.removeRoute(TEST_FLOW_PREFIX + flow.getId());
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
    public void testFlow(Long flowId, Object body, String recordId) {
        DefaultProducerTemplate template = (DefaultProducerTemplate) camelContext.createProducerTemplate();
        template.sendBodyAndProperty(TEST_FLOW_CAMEL_URI_PREFIX + flowId, body, TEST_FLOW_RECORD_ID_HEADER, recordId);
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
