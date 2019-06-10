package io.terminus.dalaran.core.model.converter.soap;

import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import io.terminus.dalaran.core.model.DalaranConverter;
import io.terminus.dalaran.core.model.converter.soap.processor.RequestConvertProcessor;
import io.terminus.dalaran.core.model.converter.soap.processor.ResponseConvertProcessor;
import io.terminus.dalaran.core.model.schema.SoapSchema;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Created by jingdi on 2019/6/6
 */
public class SoapConverter implements DalaranConverter<SoapSchema> {

    @Override
    public void toObject(ProcessorDefinition route, SoapSchema schema) {
        ResponseConvertProcessor processor = new ResponseConvertProcessor(schema.getOperationConfig());
        route.process(processor);
    }

    @Override
    public void fromObject(ProcessorDefinition route, SoapSchema schema) {
        WSDLParser parser = new WSDLParser();
        Definitions definitions = parser.parse(schema.getOperationConfig().getWsdl());
        RequestConvertProcessor processor = new RequestConvertProcessor(schema.getFields(), schema.getOperationConfig(), definitions);
        route.process(processor);
    }
}
