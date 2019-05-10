package io.terminus.dalaran.component;

import io.terminus.dalaran.DalaranProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.model.RouteDefinition;

public class BasicProcessorTest {

    protected CamelContext camelContext = new DefaultCamelContext(new SimpleRegistry());

    protected ProducerTemplate getProcessorTemplate(DalaranProcessor processor, Object config) {
        try {
            RouteDefinition route = new RouteDefinition("direct:test-script");
            camelContext.setTracing(true);
            processor.configure(route, config);
            camelContext.addRouteDefinition(route);
            camelContext.start();
            ProducerTemplate template = camelContext.createProducerTemplate();
            template.setDefaultEndpointUri("direct:test-script");
            return template;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
