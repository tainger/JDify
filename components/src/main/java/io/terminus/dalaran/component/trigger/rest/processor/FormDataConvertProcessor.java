package io.terminus.dalaran.component.trigger.rest.processor;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class FormDataConvertProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String formData = exchange.getIn().getBody(String.class);
        exchange.getOut().setBody(parseFormData(formData));
    }

    private Object parseFormData(String formData) {
        if (StringUtils.isNotEmpty(formData)) {
            Map body = Splitter.on("&").withKeyValueSeparator("=").split(formData);
            return JSON.toJSON(body);
        } else {
            return JSON.toJSON(Maps.newHashMap());
        }
    }
}
