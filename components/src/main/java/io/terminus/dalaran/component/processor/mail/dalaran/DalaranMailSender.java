package io.terminus.dalaran.component.processor.mail.dalaran;

import io.terminus.dalaran.component.processor.mail.camel.DalaranMailSenderConfig;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import io.terminus.dalaran.core.oss.*;

@Processor(
        value = "DalaranMailSender",
        order = 22,
        configType = DalaranMailSenderConfig.class
)
public class DalaranMailSender implements DalaranProcessor<DalaranMailSenderConfig> {

    @Autowired
    private OSSAccount ossAccount;

    @Override
    public void configure(ProcessorDefinition route, DalaranMailSenderConfig config) {
        route.process(new DalaranMailSenderProcessor(config, ossAccount));
    }
}
