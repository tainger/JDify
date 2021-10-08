package io.terminus.dalaran.component.soap.processor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class SoapClientProcessor implements Processor {

    private SoapClientConfig config;

    private OkHttpClient client;

    public SoapClientProcessor(SoapClientConfig config, OkHttpClient client) {
        this.config = config;
        this.client = client;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, String> headerValue = new HashMap<>();
        if (StringUtils.isNotBlank(config.getHeaders())) {
            headerValue.putAll(buildValues(exchange, config.getHeaders()));
        }
        Headers headers = Headers.of(headerValue);
        MediaType mediaType = MediaType.parse("text/xml");
        String url = "http://" + config.getConnector().getHost() + config.getPath();
        RequestBody body = RequestBody.create(mediaType, exchange.getIn().getBody().toString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .headers(headers)
                .build();
        Response response = client.newCall(request).execute();
        log.info("response: " + response.body());
        exchange.getOut().setBody(Objects.requireNonNull(response.body()).string());
    }

    private Map<String, String> buildValues(Exchange exchange, String params) {
        String contextKey = "DalaranContextExchange" + exchange.getExchangeId();
        Map<String, Object> contextValues = (Map) exchange.getProperties().get(contextKey);
        Map<String, String> values = new HashMap<>();
        values.put("Content-Type", "text/xml");
        String[] headerNames = StringUtils.split(StringUtils.replaceChars(params, " ", ""), ",");
        for (String name : headerNames) {
            String value = null;
            if (MapUtils.isNotEmpty(contextValues)) {
                value = String.valueOf(contextValues.get(name));
            }
            if (StringUtils.isBlank(value)) {
                value = String.valueOf(exchange.getIn().getHeader(name));
            }
            if (value != null) {
                values.put(name, value);
            }
        }
        log.info("headers: " + values.toString());
        return values;
    }
}
