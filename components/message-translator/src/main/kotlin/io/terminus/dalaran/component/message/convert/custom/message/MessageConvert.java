package io.terminus.dalaran.component.message.convert.custom.message;

import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.component.message.convert.custom.model.ConvertType;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Created by jingdi on 2019/3/21
 */
public interface MessageConvert {
    void convert(ProcessorDefinition route, BodyModelType modelType, ConvertType convertType);
}
