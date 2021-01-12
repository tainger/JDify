package io.terminus.dalaran.component.csb.processor.http;

import com.alibaba.csb.sdk.HttpParameters;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.lang3.StringUtils;

@Processor(
        value = "CsbHttpClient",
        order = 9,
        configType = CsbHttpClientConfig.class,
        bodyType = "JSON"
)
public class CsbHttpClient implements DalaranProcessor<CsbHttpClientConfig> {

    @Override
    public void configure(ProcessorDefinition route, CsbHttpClientConfig config) {
        HttpParameters.Builder builder = HttpParameters.newBuilder();

        builder.requestURL(config.getRequestURL()) // 设置请求的URL
                .api(config.getApi()) // 设置服务名
                .version(config.getVersion()) // 设置版本号
                .method(config.getMethod().toString());// 设置调用方式, get/post

        if (StringUtils.isNotBlank(config.getAccessKey()) && StringUtils.isNotBlank(config.getSecretKey())) {
            builder.accessKey(config.getAccessKey())
                    .secretKey(config.getSecretKey());
        }
        route.process(new CsbHttpProcessor(config,builder));
    }
}
