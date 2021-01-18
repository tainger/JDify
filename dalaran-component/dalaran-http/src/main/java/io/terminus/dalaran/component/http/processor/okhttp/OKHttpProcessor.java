package io.terminus.dalaran.component.http.processor.okhttp;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.component.http.processor.HttpClientConfig;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import okhttp3.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class OKHttpProcessor implements Processor {

    private HttpClientConfig config;

    private OkHttpClient client;

    public OKHttpProcessor(HttpClientConfig config, OkHttpClient client) {
        this.config = config;
        this.client = client;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String url = "";
        if (config.getConnector().getProtocol().name().equals("HTTPS")){
            url = config.getConnector().getProtocol().name().toLowerCase() + "://" + config.getConnector().getHost() + config.getPath();
        }else {
            url = config.getConnector().getProtocol().name().toLowerCase() + "://" + config.getConnector().getHost() + ":" + config.getConnector().getPort() + config.getPath();
        }
        log.info("url: " + url);
        Request request;
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        Headers headers = Headers.of();
        if (StringUtils.isNotBlank(config.getHeaders())) {
            headers = Headers.of(buildValues(exchange, config.getHeaders()));
        }

        if (config.getMethod() == HttpMethod.GET) {
            Map<String, Object> params = buildQueryString(exchange.getIn().getBody());
            //params.forEach(httpBuilder::addQueryParameter);
            for(String string: params.keySet()) {
                httpBuilder.addQueryParameter(string, params.get(string).toString());
            }
            request = new Request.Builder().url(httpBuilder.build()).headers(headers).build();
        } else {
            if (StringUtils.isNotBlank(config.getQueryParams())) {
                url += Joiner.on("&").withKeyValueSeparator("=").join(buildValues(exchange, config.getQueryParams()));
            }
            switch (config.getContentType()) {
                case APPLICATION_FORM_URLENCODED:
                    Map<String, String> formBody = buildFormBody(exchange.getIn().getBody());
                    FormBody.Builder form = new FormBody.Builder();
                    formBody.forEach((k, v) -> form.add(k, v));
                    FormBody requestBody = form.build();
                    request = new Request.Builder().url(url).headers(headers).post(requestBody).build();
                    break;
                case MULTIPART_FORM_DATA:
                    Map<String, String> formData = buildValues(exchange, config.getFormData());
                    var multipartBuilder = new MultipartBody.Builder();
                    formData.forEach((k, v) -> multipartBuilder.addFormDataPart(k, v));
                    MultipartBody multipartBody =  multipartBuilder.setType(MultipartBody.FORM).build();
                    request = new Request.Builder().url(url).headers(headers).post(multipartBody).build();
                    break;
                default:
                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), buildRequestBody(exchange.getIn().getBody()));
                    request = new Request.Builder().url(url).headers(headers).post(body).build();
            }
        }
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            throw new RuntimeException("Http Request Error! " + Objects.requireNonNull(response.body()).string());
        }
        String responseBody = Objects.requireNonNull(response.body()).string();
        log.info("response: " + responseBody);
        exchange.getOut().setBody(responseBody);
    }

    private Map<String, Object> buildQueryString(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }
        Map inBody;
        if (obj instanceof byte[]) {
            inBody = JSON.parseObject(IOUtils.toString((byte[]) obj), Map.class);
        } else if (obj instanceof String) {
            inBody = JSON.parseObject((String)obj, Map.class);
        } else {
            inBody = JSON.parseObject(JSON.toJSONString(obj), Map.class);
        }
        return Maps.filterEntries(inBody, (Predicate<Map.Entry>) entry -> entry.getValue() != null && entry.getKey() != null);
//        return Joiner.on("&").withKeyValueSeparator("=").join(queryKV);
    }

    private String buildRequestBody(Object body) throws Exception {
        if (body == null) {
            return null;
        }
        if (body instanceof byte[]) {
            return IOUtils.toString((byte[])body);
        } else if (body instanceof String) {
            return (String)body;
        } else {
            return JSON.toJSONString(body);
        }
    }

    private Map<String, String> buildValues(Exchange exchange, String params) {
        String contextKey = "DalaranContextExchange" + exchange.getExchangeId();
        Map<String, Object> contextValues = (Map)exchange.getProperties().get(contextKey);
        Map<String, String> values = new HashMap<>();
        String[] headerNames = StringUtils.split(StringUtils.replaceChars(params, " ", ""), ",");
        for (String name: headerNames) {
            values.put(name, (String)contextValues.get(name));
        }
        if (config.getAddLastHeaders()) {
            exchange.getIn().getHeaders().forEach((k, v) -> {
                values.put(k, String.valueOf(v));
            });
        }
        return values;
    }

    private Map<String, String> buildFormBody(Object body) throws Exception {
        if (body == null) {
            return null;
        }
        if (body instanceof byte[]) {
            return JSON.parseObject(IOUtils.toString((byte[])body), Map.class);
        } else if (body instanceof String) {
            return JSON.parseObject((String) body, Map.class);
        } else {
            return JSON.parseObject(JSON.toJSONString(body), Map.class);
        }
    }
}
