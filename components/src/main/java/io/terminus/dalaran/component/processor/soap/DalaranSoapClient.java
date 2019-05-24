package io.terminus.dalaran.component.processor.soap;

import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import io.terminus.dalaran.BodyType;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

import java.util.Map;

/**
 * Created by jingdi on 2019/5/23
 */
@Processor(value = "soap-client", configType = DalaranSoapConfig.class, serializedBody = true, allowBodyTypes = {BodyType.JSON, BodyType.XML})
public class DalaranSoapClient implements DalaranProcessor<DalaranSoapConfig> {

    @Override
    public void configure(ProcessorDefinition route, DalaranSoapConfig config) {
        WSDLParser parser = new WSDLParser();
        Definitions definitions = parser.parse(config.getWsdl());

        DalaranSoapProcessor processor = new DalaranSoapProcessor();
        route.process(processor).convertBodyTo(String.class);
    }
}
