package io.terminus.dalaran.camel.component.elasticjob;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

public class ElasticJobComponent extends DefaultComponent {

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) {
        return new ElasticJobEndpoint();
    }
}
