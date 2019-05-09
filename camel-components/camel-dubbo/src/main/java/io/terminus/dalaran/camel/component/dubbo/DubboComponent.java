package io.terminus.dalaran.camel.component.dubbo;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

public class DubboComponent extends DefaultComponent {

    private final DalaranDubboContext dalaranDubboContext = new DalaranDubboContext();

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) {
        return new DubboEndpoint(dalaranDubboContext);
    }
}
