package io.terminus.dalaran.component;

import io.terminus.dalaran.DalaranTrigger;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;

public abstract class BasicTriggerTest {

    private CamelContext camelContext = new DefaultCamelContext();

    protected void registerTrigger(DalaranTrigger trigger, Object config) {
        try {
            RouteDefinition route = new RouteDefinition();
            route.setId("test-route");
            camelContext.setTracing(true);
            trigger.buildFromRoute(route, config);
//            route.setBody(Builder.constant(SUCCESSFUL));
            route.bean(this, "process");
            camelContext.addRouteDefinition(route);
            camelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void unregisterTrigger() throws Exception {
        camelContext.stop();
    }

    public abstract Object process(Object param) throws Exception;
}
