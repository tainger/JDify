package io.terminus.dalaran.component.http.processor.okhttp;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import io.terminus.dalaran.component.http.processor.HttpClientConfig;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import okhttp3.OkHttpClient;
import org.apache.camel.model.ProcessorDefinition;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Processor(
        value = "BrotliHttpClient",
        order = 10,
        configType = HttpClientConfig.class,
        bodyType = "JSON"
)
public class OKHttpClient implements DalaranProcessor<HttpClientConfig> {

    @Override
    public void configure(ProcessorDefinition route, HttpClientConfig config) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(config.getConnector().getTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getConnector().getTimeout(), TimeUnit.MILLISECONDS)
                .sslSocketFactory(createSSLSocketFactory(),new TrustAllCertificates())
                .hostnameVerifier((s, sslSession) -> true)
                .build();
        route.process(new OKHttpProcessor(config, client));
    }

    public String buildQueryString(Object obj) {
        if (obj instanceof Map) {
            Map queryKV = Maps.filterEntries((Map) obj, (Predicate<Map.Entry>) entry -> entry.getValue() != null && entry.getKey() != null);
            return Joiner.on("&").withKeyValueSeparator("=").join(queryKV);
        }
        return null;
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TrustAllCertificates()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }

}
