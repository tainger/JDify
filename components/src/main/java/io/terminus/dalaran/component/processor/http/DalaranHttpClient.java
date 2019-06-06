package io.terminus.dalaran.component.processor.http;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranMessageBodyCustomConverter;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranConverterContext;
import io.terminus.dalaran.core.model.BodyType;
import org.apache.camel.Exchange;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Processor(
        value = "http-client", configType = HttpClientConfig.class,
        allowBodyTypes = {BodyType.JSON, BodyType.XML},
        inputSerializeType = BodySerializeType.Serialized,
        outputSerializeType = BodySerializeType.Serialized
)
public class DalaranHttpClient implements DalaranProcessor<HttpClientConfig>, DalaranMessageBodyCustomConverter<HttpClientConfig> {

    @Autowired
    private DalaranConverterContext converterContext;

    private static final String HTTP_URI = "%s4://%s:%s%s%s?bridgeEndpoint=true";

    @Override
    public boolean customBodyConvert(RouteDefinition route, HttpClientConfig config, boolean bodyIsSerialized) {
        if (!config.getMethod().isNoBody()) {
            return true;
        }
        if (bodyIsSerialized && config.getInModel().getModelType().isSerialized()) {
            converterContext.toObject(route, config.getInModel());
        }
        route.setHeader(Exchange.HTTP_QUERY).method(this, "buildQueryString");
        if (config.getInModel().getModelType().isSerialized()) {
            // 如果转为 QueryString 后, body 实际上是无用的
            route.setBody(Builder.constant(null));
        }
        return false;
    }

    // TODO form && queryString
    @Override
    public void configure(ProcessorDefinition route, HttpClientConfig config) {
        HttpClientConnector connector = config.getConnector();
        String uri = String.format(HTTP_URI, connector.getProtocol().name().toLowerCase(), connector.getHost(),
                connector.getPort(), connector.getBasePath(), config.getPath());
        route.setHeader("CamelHttpMethod", Builder.constant(config.getMethod().name()));
        route.to(uri);
        // TODO Stream to string
        route.convertBodyTo(String.class);
    }

    public String buildQueryString(Object obj) {
        if (obj instanceof Map) {
            Map queryKV = Maps.filterEntries((Map) obj, (Predicate<Map.Entry>) entry -> entry.getValue() != null && entry.getKey() != null);
            return Joiner.on("&").withKeyValueSeparator("=").join(queryKV);
        }
        return null;
    }
}
