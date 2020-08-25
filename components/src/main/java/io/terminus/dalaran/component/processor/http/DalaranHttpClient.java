package io.terminus.dalaran.component.processor.http;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.component.connector.RestClientConnector;
import io.terminus.dalaran.core.component.DalaranMessageBodyCustomConverter;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranModelTypeContext;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.apache.camel.Exchange.*;

@Processor(
        value = "http-client",
        order = 11,
        configType = HttpClientConfig.class,
        bodyType = "JSON"
)
public class DalaranHttpClient implements DalaranProcessor<HttpClientConfig>, DalaranMessageBodyCustomConverter<HttpClientConfig> {

    @Autowired
    private DalaranModelTypeContext converterContext;

    private static final String HTTP_URI = "%s4://%s:%s%s?bridgeEndpoint=true";

    @Override
    public boolean customBodyConvert(RouteDefinition route, HttpClientConfig config, String currentBodyType) {
        if (!config.getMethod().isNoBody()) {
            return true;
        }
        if (!DalaranConstants.OBJECT_MODEL_TYPE.equalsIgnoreCase(currentBodyType) && !DalaranConstants.UNKNOWN_MODEL_TYPE.equalsIgnoreCase(currentBodyType)) {
            converterContext.toObject(route, config.getInModel(), currentBodyType);
        }
        route.setHeader(HTTP_QUERY).method(this, "buildQueryString");
        // 如果转为 QueryString 后, body 实际上是无用的
//        route.setBody(Builder.constant(null));
        return false;
    }

    // TODO form && queryString
    @Override
    public void configure(ProcessorDefinition route, HttpClientConfig config) {
        RestClientConnector connector = config.getConnector();
        String uri = String.format(HTTP_URI, connector.getProtocol().name().toLowerCase(), connector.getHost(),
                connector.getPort(), config.getPath());
        if (StringUtils.isNotBlank(config.getConnector().getUsername()) && StringUtils.isNotBlank(config.getConnector().getPassword())) {
            uri = uri + "&authMethod=Basic&authUsername=" + config.getConnector().getUsername() + "&authPassword=" + config.getConnector().getPassword();
        }
        route.setHeader(HTTP_METHOD, Builder.constant(config.getMethod().name()));
        route.setHeader(CONTENT_TYPE, Builder.constant("application/json"));
//        if (config.getMethod().isNoBody()) {
//            route.process(new QueryStringProcessor());
//        }
        if (!config.getMethod().isNoBody() && StringUtils.isNotBlank(config.getHeaders())) {
            route.process(new QueryHeadersProcessor(config.getHeaders()));
        }
        if (!config.getMethod().isNoBody() && StringUtils.isNotBlank(config.getQueryParams())) {
            route.process(new QueryParamProcessor(config.getQueryParams()));
        }
        if (config.getMethod().isNoBody()) {
            route.process(new QueryStringProcessor());
        }
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
