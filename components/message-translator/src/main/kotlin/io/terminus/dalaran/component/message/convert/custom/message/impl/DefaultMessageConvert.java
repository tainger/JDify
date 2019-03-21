package io.terminus.dalaran.component.message.convert.custom.message.impl;

import io.terminus.dalaran.component.message.convert.custom.message.MessageConvert;
import io.terminus.dalaran.component.message.convert.custom.model.ConvertType;
import io.terminus.dalaran.component.message.convert.custom.model.ModelType;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.dataformat.XmlJsonDataFormat;

/**
 * Created by jingdi on 2019/3/21
 */
public class DefaultMessageConvert implements MessageConvert {

    @Override
    public void convert(ProcessorDefinition route, ModelType modelType, ConvertType convertType) {
        switch (convertType) {
            case TARGET:
                convertIn(route, modelType);
                break;
            case DESTINATION:
                convertOut(route, modelType);
        }
    }

    private void convertIn(ProcessorDefinition route, ModelType type) {
        switch (type) {
            case JSON:
                route.convertBodyTo(String.class);
                break;
            case XML:
                XmlJsonDataFormat xmlJsonFormat = new XmlJsonDataFormat();
                xmlJsonFormat.setForceTopLevelObject(true);
                route.marshal(xmlJsonFormat).convertBodyTo(String.class);
                break;
            default:
                route.convertBodyTo(String.class);
        }
    }

    private void convertOut(ProcessorDefinition route, ModelType type) {
        switch (type) {
            case JSON:
                route.marshal().json(JsonLibrary.Gson);
                break;
            default:
                route.marshal().json(JsonLibrary.Gson);
        }
    }
}
