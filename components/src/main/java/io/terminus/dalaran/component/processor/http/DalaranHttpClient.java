package io.terminus.dalaran.component.processor.http;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import io.terminus.dalaran.BodyType;
import io.terminus.dalaran.DalaranConverterContext;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.Processor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Processor(value = "http-client", configType = HttpClientConfig.class, serializedBody = true, allowBodyTypes = {BodyType.JSON, BodyType.XML})
public class DalaranHttpClient implements DalaranProcessor<HttpClientConfig> {

    @Autowired
    private DalaranConverterContext converterContext;

    private static final String HTTP_URI = "%s4://%s:%s%s?bridgeEndpoint=true";

    // TODO form && queryString
    @Override
    public void configure(ProcessorDefinition route, HttpClientConfig config) {
        HttpClientConnector connector = config.getConnector();
        String uri = String.format(HTTP_URI, connector.getProtocol().name().toLowerCase(), connector.getHost(), connector.getPort(), config.getPath());
        route.setHeader("CamelHttpMethod", Builder.constant(config.getMethod().name()));
        if (config.getMethod().isNoBody()) {
            // TODO 这里有可能导致序列化之后又反序列化回来, 如果不在这里处理, 就要扔到外面, 回头处理一下
            if (config.getInModel().getModelType().isSerialized()) {
                converterContext.toObject(route, config.getInModel());
            }
            route.setHeader(Exchange.HTTP_QUERY).method(this, "buildQueryString");
            // TODO 这里💥奇怪, 但是输入一定要是个流...
            if (config.getInModel().getModelType().isSerialized()) {
                route.marshal().json(JsonLibrary.Fastjson);
            }
        }
        route.to(uri);
        // TODO Stream to string
        route.convertBodyTo(String.class);
    }

    public String buildQueryString(Object obj) {
        if (obj instanceof Map) {
            Map queryKV = Maps.filterEntries((Map) obj, (Predicate<Map.Entry>) entry -> entry.getValue() != null && entry.getKey() != null);
            return "?" + Joiner.on("&").withKeyValueSeparator("=").join(queryKV);
        }
        return null;
    }
}
