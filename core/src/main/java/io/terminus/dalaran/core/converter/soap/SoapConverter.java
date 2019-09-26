package io.terminus.dalaran.core.converter.soap;

import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import io.terminus.dalaran.core.converter.DalaranConverter;
import io.terminus.dalaran.core.converter.soap.processor.ObjectToSoapProcessor;
import io.terminus.dalaran.core.converter.soap.processor.SoapToObjectProcessor;
import io.terminus.dalaran.model.schema.SoapSchema;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.io.IOUtils;

/**
 * Created by jingdi on 2019/6/6
 */
public class SoapConverter implements DalaranConverter<SoapSchema> {

    @Override
    public void toObject(ProcessorDefinition route, SoapSchema schema) {
        SoapToObjectProcessor processor = new SoapToObjectProcessor(schema.getOperationConfig());
        route.process(processor);
    }

    @Override
    public void fromObject(ProcessorDefinition route, SoapSchema schema) {
        ObjectToSoapProcessor processor = new ObjectToSoapProcessor(schema.getFields(), schema.getOperationConfig());
        route.process(processor);
    }
}
