package io.terminus.dalaran.component.processor.http.brotli;

import io.terminus.dalaran.component.processor.http.HttpClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Slf4j
public class BrotliHttpProcessor implements Processor {

    private HttpClientConfig config;

    public BrotliHttpProcessor(HttpClientConfig config) {
        this.config = config;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String url = "http://" + config.getConnector().getHost() + config.getPath();
        log.info("url: " + url);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpUriRequest httpUriRequest = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpUriRequest);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            log.info("result: " + result);
            exchange.getOut().setBody(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
