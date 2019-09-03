package io.terminus.dalaran.component.processor.mapper;

import io.terminus.dalaran.component.processor.mapper.jsonPath.Converter;
import io.terminus.dalaran.component.processor.mapper.model.DalaranMappingConfig;
import io.terminus.dalaran.component.processor.mapper.model.MapperConstants;
import io.terminus.dalaran.core.context.DalaranContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Traceable;
import org.apache.commons.collections.CollectionUtils;

import java.util.Map;

/**
 * Created by jingdi on 2019/3/18
 */
public class DalaranMapperProcessor implements Processor, Traceable {

    private final DalaranMappingConfig mappingConfig;

    private final DalaranContext dalaranContext;

    public DalaranMapperProcessor(DalaranMappingConfig mappingConfig, DalaranContext dalaranContext) {
        this.mappingConfig = mappingConfig;
        this.dalaranContext = dalaranContext;
    }

    @Override
    public void process(Exchange exchange) {
        Object source = exchange.getIn().getBody();
        Object destination = (mappingConfig == null || CollectionUtils.isEmpty(mappingConfig.getMessageMappings())) ? source : convert(mappingConfig, source);
        exchange.getOut().setBody(destination);
    }

    public Object convert(DalaranMappingConfig mappingConfig, Object source) {
        Map<String, Object> destination = Converter.convert(mappingConfig, source, dalaranContext);
        return destination.get(MapperConstants.MODEL_ROOT);
    }

    @Override
    public String getTraceLabel() {
        return "DalaranMapper";
    }
}
