package io.terminus.dalaran.component.soap.processor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Objects;

@Slf4j
public class SoapClientProcessor implements Processor {

    private SoapClientConfig config;

    private OkHttpClient client;

    public SoapClientProcessor(SoapClientConfig config, OkHttpClient client) {
        this.config = config;
        this.client = client;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        MediaType mediaType = MediaType.parse("text/xml");
        String url = "http://" + config.getConnector().getHost() + config.getPath();
        RequestBody body = RequestBody.create(mediaType, exchange.getIn().getBody().toString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "text/xml")
                .build();
        Response response = client.newCall(request).execute();
        log.info("response: " + response.body());
        exchange.getOut().setBody(Objects.requireNonNull(response.body()).string());
    }
}
