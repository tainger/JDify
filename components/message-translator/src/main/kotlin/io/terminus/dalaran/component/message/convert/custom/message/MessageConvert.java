package io.terminus.dalaran.component.message.convert.custom.message;

import io.terminus.dalaran.component.message.convert.custom.model.ConvertType;
import io.terminus.dalaran.model.ModelType;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Created by jingdi on 2019/3/21
 */
public interface MessageConvert {
    void convert(ProcessorDefinition route, ModelType modelType, ConvertType convertType);
}
