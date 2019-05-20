package io.terminus.dalaran.component.processor.http;

import com.alibaba.dubbo.common.utils.IOUtils;
import com.alibaba.fastjson.JSON;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.terminus.dalaran.DalaranComponentConfigImporter;
import io.terminus.dalaran.component.common.HttpMethod;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class HttpConfigImporter implements DalaranComponentConfigImporter<HttpClientConfig, HttpSwaggerConfig> {
    @Override
    public HttpClientConfig importConfig(HttpSwaggerConfig importConfig) {

        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpUriRequest httpUriRequest = new HttpGet(importConfig.getSwaggerUrl());
        try {
            HttpResponse response = httpClient.execute(httpUriRequest);
            Swagger swagger = JSON.parseObject(response.getEntity().getContent(), Swagger.class);
            Path path = swagger.getPath(importConfig.getPath());
            Operation operation = path.getOperationMap().get(importConfig.getMethod());
            operation.getResponses().get("200").getSchema().getType();

            HttpClientConfig config = new HttpClientConfig();
            config.setMethod(HttpMethod.valueOf(importConfig.getMethod()));
            config.setPath(importConfig.getPath());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
