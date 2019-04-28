package io.terminus.dalaran.support.flow;

import io.terminus.dalaran.DalaranComponentContext;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.DalaranConverterContext;
import io.terminus.dalaran.model.flow.TriggerFlow;
import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

import static io.terminus.dalaran.DalaranConstants.*;

public class DefaultDalaranCamelContext implements DalaranContext {

    private final FlowBuilder flowBuilder;
    private final CamelContext camelContext;
    private final DalaranConverterContext converterContext;
    private final DalaranComponentContext componentContext;


    public DefaultDalaranCamelContext(FlowBuilder flowBuilder, DalaranConverterContext converterContext, DalaranComponentContext componentContext) {
        this.flowBuilder = flowBuilder;
        this.converterContext = converterContext;
        this.componentContext = componentContext;
        this.camelContext = new DefaultCamelContext();
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

    // TODO 这里要处理数据的序列化等问题
    @Override
    public Object testFlow(Long flowId, Object body, String recordId) {
        DefaultProducerTemplate template = (DefaultProducerTemplate) camelContext.createProducerTemplate();
        return template.sendBodyAndProperty(TEST_FLOW_CAMEL_URI_PREFIX + flowId, ExchangePattern.InOut, body, TEST_FLOW_RECORD_ID_HEADER, recordId);
    }

    @Override
    public DalaranComponentContext getDalaranComponentContext() {
        return componentContext;
    }

    @Override
    public DalaranConverterContext getDalaranConverterContext() {
        return converterContext;
    }

}
