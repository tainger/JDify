package io.terminus.dalaran.camel.component.dubbo;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

public class DubboComponent extends DefaultComponent {

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) {
        System.out.println("createEndpoint start");
        return new DubboEndpoint();
    }

    @Override
    protected void doStart() throws Exception {
        System.out.println("DubboComponent start");
        super.doStart();
        System.out.println("DubboComponent done");
    }
}
