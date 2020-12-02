package io.terminus.dalaran.component.http.trigger.processor;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import io.terminus.dalaran.component.http.trigger.RestConfig;
import io.terminus.dalaran.component.utils.AESUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static io.terminus.dalaran.component.http.trigger.utils.SignUtils.stopExchangeOnMissingAppKey;

public class AESQuerySignProcessor implements Processor {

    private RestConfig config;

    public AESQuerySignProcessor(RestConfig config) {
        this.config = config;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String queryString = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
        if (StringUtils.isEmpty(queryString)) {
            stopExchangeOnMissingAppKey(exchange);
            return;
        }
        Map body = Splitter.on("&").withKeyValueSeparator("=").split(queryString);
        Object out = JSON.parse(AESUtils.decrypt(JSON.toJSONString(body), config.getSecret()));
        exchange.getOut().setBody(out);
    }
}
