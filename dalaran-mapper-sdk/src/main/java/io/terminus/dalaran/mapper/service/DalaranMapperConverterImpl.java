package io.terminus.dalaran.mapper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.terminus.dalaran.mapper.handler.Converter;
import io.terminus.dalaran.mapper.handler.DalaranMapperBuilder;
import io.terminus.dalaran.mapper.context.DalaranFunctionContext;
import io.terminus.dalaran.mapper.model.DalaranMapperConfig;
import io.terminus.dalaran.mapper.model.DalaranMappingConfig;
import io.terminus.dalaran.mapper.model.MapperConstants;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DalaranMapperConverterImpl implements DalaranMapperConverter {

    @Autowired
    private DalaranFunctionContext functionContext;

    @Override
    public <T> T convert(Object source, DalaranMapperConfig mapperConfig, Class<T> outClass) {
        Object destination = convert(source, mapperConfig);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(destination, outClass);
    }

    @Override
    public Object convert(Object source, DalaranMapperConfig mapperConfig) {
        DalaranMapperBuilder mapperBuilder = new DalaranMapperBuilder(functionContext);
        DalaranMappingConfig mappingConfig = mapperBuilder.transfer(mapperConfig.getMessageMapping(), mapperConfig.getInModel(), mapperConfig.getOutModel());
        return (mappingConfig == null || CollectionUtils.isEmpty(mappingConfig.getMessageMappings())) ? source : Converter.convert(source, mappingConfig, functionContext).get(MapperConstants.MODEL_ROOT);
    }
}
