package io.terminus.dalaran.component;

import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.component.model.ServiceOperation;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.model.RouteDefinition;

/**
 * Created by jingdi on 2019/6/25
 */
public class BasicServiceTest {

    protected CamelContext camelContext = new DefaultCamelContext(new SimpleRegistry());

    protected ProducerTemplate getProcessorTemplate(DalaranService service, ServiceOperation config) {
        try {
            RouteDefinition route = new RouteDefinition("direct:test-script");
            route.setId("test-route");
            camelContext.setTracing(true);
            service.configure(route, config);
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
