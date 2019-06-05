package io.terminus.dalaran.component.processor.soap;

import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.model.BodyType;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Created by jingdi on 2019/5/23
 */
@Processor(value = "soap-client", configType = DalaranSoapConfig.class,
        inputSerializeType = BodySerializeType.Serialized,
        outputSerializeType = BodySerializeType.Serialized,
        allowBodyTypes = {BodyType.JSON, BodyType.XML})
public class DalaranSoapClient implements DalaranProcessor<DalaranSoapConfig> {

    @Override
    public void configure(ProcessorDefinition route, DalaranSoapConfig config) {
        WSDLParser parser = new WSDLParser();
        Definitions definitions = parser.parse("http://127.0.0.1:8081/ws/countries.wsdl");
        DalaranSoapProcessor processor = new DalaranSoapProcessor(null, definitions);
        route.process(processor);
    }
}
