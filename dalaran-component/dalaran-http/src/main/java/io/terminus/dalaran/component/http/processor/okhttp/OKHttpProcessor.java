package io.terminus.dalaran.component.http.processor.okhttp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import io.terminus.dalaran.component.common.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import okhttp3.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
public class OKHttpProcessor implements Processor {

    private OKHttpClientConfig config;

    private OkHttpClient client;

    private final List<Integer> SUCCESS_CODE = Arrays.asList(200, 201, 202, 203, 204, 205, 206);


    public OKHttpProcessor(OKHttpClientConfig config, OkHttpClient client) {
        this.config = config;
        this.client = client;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String path = buildPath(config.getPath(), exchange);
        String url;
        if (config.getConnector().getProtocol().name().equals("HTTPS")) {
            url = config.getConnector().getProtocol().name().toLowerCase() + "://" + config.getConnector().getHost() + path;
        } else {
            url = config.getConnector().getProtocol().name().toLowerCase() + "://" + config.getConnector().getHost() + ":" + config.getConnector().getPort() + path;
        }
        Request request;
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        Headers headers = Headers.of();
        if (StringUtils.isNotBlank(config.getHeaders())) {
            headers = Headers.of(buildValues(exchange, config.getHeaders()));
        }
        if (config.getMethod() == HttpMethod.GET) {
            Map<String, Object> params = buildQueryString(exchange.getIn().getBody());
            //params.forEach(httpBuilder::addQueryParameter);
            for (String string : params.keySet()) {
                httpBuilder.addQueryParameter(string, params.get(string).toString());
            }
            request = new Request.Builder().url(httpBuilder.build()).headers(headers).build();
        } else {
            if (StringUtils.isNotBlank(config.getQueryParams())) {
                if (!StringUtils.endsWith(url, "?")) {
                    url += "?";
                }
                url += Joiner.on("&").withKeyValueSeparator("=").join(buildValues(exchange, config.getQueryParams()));
            }
            switch (config.getContentType()) {
                case APPLICATION_FORM_URLENCODED:
                    Map<String, String> formBody = buildFormBody(exchange.getIn().getBody());
                    FormBody.Builder form = new FormBody.Builder();
                    formBody.forEach((k, v) -> form.add(k, v));
                    FormBody requestBody = form.build();
                    request = makeRequest(url, headers, config.getMethod(), requestBody);
                    break;
                case MULTIPART_FORM_DATA:
                    Map<String, String> formData = buildValues(exchange, config.getFormData());
                    var multipartBuilder = new MultipartBody.Builder();
                    formData.forEach((k, v) -> multipartBuilder.addFormDataPart(k, v));
                    MultipartBody multipartBody = multipartBuilder.setType(MultipartBody.FORM).build();
                    request = makeRequest(url, headers, config.getMethod(), multipartBody);
                    break;
                default:
                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), buildRequestBody(exchange.getIn().getBody()));
                    request = makeRequest(url, headers, config.getMethod(), body);
            }
        }
        Response response = client.newCall(request).execute();
        String responseBody = Objects.requireNonNull(response.body()).string();
        log.info("response: " + responseBody);
        if (!SUCCESS_CODE.contains(response.code())) {
            throw new RuntimeException("Http Request Error! " + responseBody);
        }
        exchange.getOut().setBody(responseBody);
    }

    public Request makeRequest(String url, Headers headers, HttpMethod method, RequestBody requestBody) {
        Request request;
        if (method == HttpMethod.PUT) {
            request = new Request.Builder().url(url).headers(headers).put(requestBody).build();
        } else if (method == HttpMethod.DELETE) {
            request = new Request.Builder().url(url).headers(headers).delete(requestBody).build();
        } else {
            request = new Request.Builder().url(url).headers(headers).post(requestBody).build();
        }
        return request;
    }

    private Map<String, Object> buildQueryString(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }
        Map inBody;
        if (obj instanceof byte[]) {
            inBody = JSON.parseObject(IOUtils.toString((byte[]) obj), Map.class);
        } else if (obj instanceof String) {
            inBody = JSON.parseObject((String) obj, Map.class);
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
            return IOUtils.toString((byte[]) body);
        } else if (body instanceof String) {
            return (String) body;
        } else {
            return JSON.toJSONString(body);
        }
    }

    private Map<String, String> buildValues(Exchange exchange, String params) {
        String contextKey = "DalaranContextExchange" + exchange.getExchangeId();
        Map<String, Object> contextValues = (Map) exchange.getProperties().get(contextKey);
        Map<String, String> values = new HashMap<>();
        String[] headerNames = StringUtils.split(StringUtils.replaceChars(params, " ", ""), ",");
        for (String name : headerNames) {
            values.put(name, (String) contextValues.get(name));
        }
//        if (config.getAddLastHeaders()) {
//            exchange.getIn().getHeaders().forEach((k, v) -> {
//                values.put(k, String.valueOf(v));
//            });
//        }
        return values;
    }

    private Map<String, String> buildFormBody(Object body) throws Exception {
        if (body == null) {
            return null;
        }
        String toParse;
        if (body instanceof byte[]) {
            toParse = IOUtils.toString((byte[]) body);
        } else if (body instanceof String) {
            toParse = (String) body;
        } else {
            toParse = JSON.toJSONString(body);
        }
        //省一次JSONObject.parse
        try {
            return JSONObject.parseObject(toParse, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    private String buildPath(String path, Exchange exchange) throws Exception {
        Object body = exchange.getIn().getBody();
        Map<String, String> bodyParameter = buildFormBody(body);
        if (!CollectionUtils.isEmpty(bodyParameter)) {
            if (!StringUtils.contains(path, "{") || !StringUtils.contains(path, "}")) {
                return path;
            }
            String contextKey = "DalaranContextExchange" + exchange.getExchangeId();
            Map<String, Object> contextValues = (Map) exchange.getProperties().get(contextKey);
            StringBuilder pathBuilder = new StringBuilder(path);
            String[] params = StringUtils.split(path, "/");
            for (String param : params) {
                if (!StringUtils.contains(param, "{") || !StringUtils.contains(param, "}")) {
                    continue;
                }
                String paramKey = StringUtils.substring(param, 1, param.length() - 1);
                if (contextValues.containsKey(paramKey) || bodyParameter.containsKey(paramKey)) {
                    String pathValue = bodyParameter.get(paramKey);
                    if (null == pathValue) {
                        pathValue = contextValues.get(paramKey).toString();
                    }
                    pathBuilder = new StringBuilder(StringUtils.replace(pathBuilder.toString(), param, pathValue));
                } else {
                    throw new RuntimeException("parameter in url path not configure: " + paramKey);
                }
            }
            return pathBuilder.toString();
        }
        return path;
    }
}
