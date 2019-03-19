package io.terminus.dalaran.console.autoconfigura;

import io.terminus.dalaran.DalaranComponentContainer;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.impl.DefaultDalaranCamelContext;
import io.terminus.dalaran.impl.DefaultDalaranComponentContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DalaranAutoConfiguration {

    @Bean
    public DalaranContext dalaranContext(DalaranComponentContainer dalaranComponentContainer) {
        return new DefaultDalaranCamelContext(dalaranComponentContainer);
    }

    @Bean
    public DalaranComponentContainer dalaranComponentContainer() {
        return new DefaultDalaranComponentContainer();
    }
}
