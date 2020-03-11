package io.terminus.dalaran.core.component.config;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.Registry;

public class CustomCamelContext extends DefaultCamelContext {

    public CustomCamelContext() {
        super();
        super.setUuidGenerator(new DalaranUuidGenerator());
    }

    public CustomCamelContext(Registry registry) {
        super(registry);
        super.setUuidGenerator(new DalaranUuidGenerator());
    }
}
