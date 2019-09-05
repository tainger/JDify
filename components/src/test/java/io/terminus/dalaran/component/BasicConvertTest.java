package io.terminus.dalaran.component;

import io.terminus.dalaran.component.convert.TestProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

public class BasicConvertTest {

    protected CamelContext camelContext = new DefaultCamelContext(new SimpleRegistry());

    protected ProducerTemplate getConvertTemplate(List<TestProcessor> processors) {
        try {
            RouteDefinition route = new RouteDefinition("direct:test-script");
            route.setId("test-route");
            camelContext.setTracing(true);
            processors.forEach(processor -> {
                processor.getProcessor().configure(route, processor.getConfig());
            });
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
