package io.terminus.dalaran.component.processor.http.brotli;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import io.terminus.dalaran.component.processor.http.HttpClientConfig;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranModelTypeContext;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Processor(
        value = "BrotliHttpClient",
        order = 10,
        configType = HttpClientConfig.class,
        bodyType = "JSON"
)
public class BrotliHttpClient implements DalaranProcessor<HttpClientConfig> {

    @Autowired
    private DalaranModelTypeContext converterContext;

    private static final String HTTP_URI = "%s4://%s:%s%s?bridgeEndpoint=true";


    @Override
    public void configure(ProcessorDefinition route, HttpClientConfig config) {
        route.process(new BrotliHttpProcessor(config));
    }

    public String buildQueryString(Object obj) {
        if (obj instanceof Map) {
            Map queryKV = Maps.filterEntries((Map) obj, (Predicate<Map.Entry>) entry -> entry.getValue() != null && entry.getKey() != null);
            return Joiner.on("&").withKeyValueSeparator("=").join(queryKV);
        }
        return null;
    }
}
